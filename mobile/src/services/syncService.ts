import axios from 'axios';

const API_BASE = 'http://10.0.2.2:8080/api';

export const syncService = {
  async fullSync(sourceId: string): Promise<SyncResponse> {
    const response = await axios.post(`${API_BASE}/sync/${sourceId}/full`);
    return response.data;
  },

  async incrementalSync(sourceId: string, cursor?: string): Promise<SyncResponse> {
    const response = await axios.post(`${API_BASE}/sync/${sourceId}/incremental`, {
      cursor
    });
    return response.data;
  },

  async pauseSync(sourceId: string): Promise<void> {
    await axios.post(`${API_BASE}/sync/${sourceId}/pause`);
  },

  async resumeSync(sourceId: string): Promise<void> {
    await axios.post(`${API_BASE}/sync/${sourceId}/resume`);
  },

  async getSyncStatus(sourceId: string): Promise<SyncStatusResponse> {
    const response = await axios.get(`${API_BASE}/sync/${sourceId}/status`);
    return response.data;
  },

  async syncAll(): Promise<SyncResponse[]> {
    const response = await axios.post(`${API_BASE}/sync/all`);
    return response.data;
  }
};

export interface SyncResponse {
  sourceId: string;
  changeType: 'FULL' | 'INCREMENTAL';
  assetCount: number;
  newCount: number;
  modifiedCount: number;
  deletedCount: number;
  cursor?: string;
  deltas: AssetDelta[];
}

export interface AssetDelta {
  assetId: string;
  changeType: 'NEW' | 'MODIFIED' | 'DELETED';
  asset?: any;
  modifiedAt: string;
}

export interface SyncStatusResponse {
  sourceId: string;
  state: 'INITIAL' | 'INCREMENTAL' | 'SYNCING' | 'ERROR' | 'PAUSED';
  lastSyncAt?: string;
  cursor?: string;
}

export default syncService;