import {
  PermissionsAndroid,
  Platform,
  NativeModules,
  NativeEventEmitter,
  useCallback,
  useEffect,
  useRef,
  useState
} from 'react-native';
import { useStore } from '../store/useStore';
import { syncService } from '../services/api';

export const useAutoSync = () => {
  const { sources, syncState, addAsset, updateAsset, removeAsset, setSyncState } = useStore();
  const [isSyncing, setIsSyncing] = useState(false);
  const [lastSyncTime, setLastSyncTime] = useState<Date | null>(null);
  const syncQueue = useRef<SyncQueueItem[]>([]);
  const isAppForeground = useRef(true);

  useEffect(() => {
    initializeSync();
  }, []);

  useEffect(() => {
    if (isAppForeground.current) {
      triggerIncrementalSync();
    }
  }, [isAppForeground]);

  const initializeSync = async () => {
    for (const source of sources) {
      await performFullSync(source.id);
    }
  };

  const performFullSync = async (sourceId: string) => {
    setIsSyncing(true);
    setSyncState(sourceId, 'SYNCING');
    
    try {
      const result = await syncService.fullSync(sourceId);
      
      for (const delta of result.deltas) {
        if (delta.changeType === 'NEW') {
          await addAsset(delta.asset);
        } else if (delta.changeType === 'MODIFIED') {
          await updateAsset(delta.asset);
        } else if (delta.changeType === 'DELETED') {
          await removeAsset(delta.assetId);
        }
      }
      
      setSyncState(sourceId, 'INCREMENTAL');
      setLastSyncTime(new Date());
    } catch (error) {
      console.error('Full sync failed:', error);
      setSyncState(sourceId, 'ERROR');
    } finally {
      setIsSyncing(false);
    }
  };

  const triggerIncrementalSync = async () => {
    if (isSyncing || !isAppForeground.current) return;
    
    setIsSyncing(true);
    
    try {
      for (const source of sources) {
        const cursor = syncState[source.id]?.cursor;
        
        const result = await syncService.incrementalSync(source.id, cursor);
        
        if (result.newCount > 0 || result.modifiedCount > 0 || result.deletedCount > 0) {
          setSyncState(source.id, 'SYNCING');
          
          for (const delta of result.deltas) {
            switch (delta.changeType) {
              case 'NEW':
                addAsset(delta.asset);
                break;
              case 'MODIFIED':
                updateAsset(delta.asset);
                break;
              case 'DELETED':
                removeAsset(delta.assetId);
                break;
            }
          }
          
          setLastSyncTime(new Date());
        }
        
        setSyncState(source.id, 'INCREMENTAL');
      }
    } catch (error) {
      console.error('Incremental sync failed:', error);
    } finally {
      setIsSyncing(false);
    }
  };

  const performManualSync = async (sourceId?: string) => {
    if (sourceId) {
      await performFullSync(sourceId);
    } else {
      for (const source of sources) {
        await performFullSync(source.id);
      }
    }
  };

  const pauseSync = async (sourceId: string) => {
    setSyncState(sourceId, 'PAUSED');
    await syncService.pauseSync(sourceId);
  };

  const resumeSync = async (sourceId: string) => {
    setSyncState(sourceId, 'INCREMENTAL');
    await syncService.resumeSync(sourceId);
  };

  return {
    isSyncing,
    lastSyncTime,
    performManualSync,
    pauseSync,
    resumeSync,
    triggerIncrementalSync
  };
};

interface SyncQueueItem {
  sourceId: string;
  assetId: string;
  changeType: 'NEW' | 'MODIFIED' | 'DELETED';
  timestamp: Date;
}

export const useSyncScheduler = () => {
  const intervalRef = useRef<NodeJS.Timeout | null>(null);
  const { triggerIncrementalSync } = useAutoSync();

  useEffect(() => {
    startScheduler();
    
    return () => {
      if (intervalRef.current) {
        clearInterval(intervalRef.current);
      }
    };
  }, []);

  const startScheduler = () => {
    const SYNC_INTERVAL = 5 * 60 * 1000;
    
    intervalRef.current = setInterval(() => {
      triggerIncrementalSync();
    }, SYNC_INTERVAL);
  };

  const stopScheduler = () => {
    if (intervalRef.current) {
      clearInterval(intervalRef.current);
    }
  };
};

export const useFileSystemWatcher = () => {
  const { addAsset } = useStore();

  useEffect(() => {
    if (Platform.OS !== 'ios') return;
    
    setupWatcher();
  }, []);

  const setupWatcher = async () => {
    try {
      const granted = await PermissionsAndroid.request(
        PermissionsAndroid.PERMISSIONS.READ_EXTERNAL_STORAGE,
        {
          title: 'Storage Permission',
          message: 'App needs access to storage for file sync',
          buttonNeutral: 'Ask Me Later',
          buttonNegative: 'Cancel',
          buttonPositive: 'OK'
        }
      );
      
      if (granted === PermissionsAndroid.RESULTS.GRANTED) {
        // Watch for file changes
      }
    } catch (err) {
      console.warn('Permission error:', err);
    }
  };
};

export default useAutoSync;