import axios from 'axios';
import { envConfig } from '../../config/env';

export const axiosClient = axios.create({
  baseURL: envConfig.API_BASE,
  timeout: 10_000
});

axiosClient.interceptors.response.use(
  (response) => response,
  (error) => {
    const message =
      error.response?.data?.message ??
      error.response?.data ??
      error.message ??
      'Unexpected error';
    return Promise.reject(new Error(message));
  }
);