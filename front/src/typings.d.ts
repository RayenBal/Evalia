declare module 'feather-icons';

declare module 'event-source-polyfill' {
  export interface EventSourceInitDict {
    withCredentials?: boolean;
    headers?: Record<string, string>;
    heartbeatTimeout?: number;
  }

  export class EventSourcePolyfill {
    constructor(url: string, init?: EventSourceInitDict);
    close(): void;

    onopen: ((e: MessageEvent) => void) | null;
    onmessage: ((e: MessageEvent) => void) | null;
    onerror: ((e: any) => void) | null;

    addEventListener(type: string, listener: (e: MessageEvent) => void): void;
    removeEventListener(type: string, listener: (e: MessageEvent) => void): void;
  }

  export const NativeEventSource: typeof EventSource | undefined;
  export default EventSourcePolyfill;
}