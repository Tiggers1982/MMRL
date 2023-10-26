export {};

declare module '*.d.ts' {
  const value: string;
  export default value;
}

declare global {
  type arr<T> = Array<T>;
  type str = string;
  type Str = String;
  type int = number;
  type Int = Number;
  type Void = void;
  type Any = any;
  type bool = boolean;

  type HTMLAttributes<E, P = {}> = React.DetailedHTMLProps<React.HTMLAttributes<E> & P, E>;
  type AnchorHTMLAttributes<E, P = {}> = React.DetailedHTMLProps<React.AnchorHTMLAttributes<E> & P, E>;

  type VersionType = `${string}.${string}.${string}`;

  namespace JSX {
    interface IntrinsicElements {
      "mmrl-anchor": React.DetailedHTMLProps<React.AnchorHTMLAttributes<HTMLAnchorElement> & { page?: string }, HTMLAnchorElement>;

      // Onsen Elements
      "ons-toolbar-button": HTMLAttributes<HTMLElement>;
      "ons-toolbar": HTMLAttributes<HTMLElement>;
      "ons-page": HTMLAttributes<HTMLElement>;
      "ons-splitter": HTMLAttributes<HTMLElement>;
      "ons-splitter-content": HTMLAttributes<HTMLElement>;
      "ons-splitter-side": HTMLAttributes<HTMLElement>;
      "ons-navigator": HTMLAttributes<HTMLElement>;
      "ons-tabbar": HTMLAttributes<HTMLElement>;
      "ons-tab": HTMLAttributes<HTMLElement>;
      "ons-gesture-detector": HTMLAttributes<HTMLElement>;
      "ons-bottom-toolbar": HTMLAttributes<HTMLElement>;
    }
  }

  /**
   * Native window properties for Android
   */
  interface AndroidWindow<I = any> {
    /**object
     * This is an Android only window object
     */
    readonly __sufile__: I;
    /**
     * This is an Android only window object
     */
    readonly __environment__: I;

    /**
     * This is an Android only window object
     */
    readonly __shell__: I;
    /**
     * This is an Android only window object
     */
    readonly __buildconfig__: I;
    /**
     * This is an Android only window object
     */
    readonly __os__: I;
    readonly __log__: I;
    readonly __properties__: I;
    /**
     * `localStorage` like object to make support better with `useLocalStorage`.
     *
     * - This interface is not configurable
     */
    readonly __nativeStorage__: Pick<Storage, "getItem" | "setItem" | "removeItem" | "clear"> & { defineName: (name: string) => void };
  }

  type TerminalExec = {
    command: string;
    env: Record<string, string>;
    onLine: (line: string) => void;
    onExit: (code: number) => void;
  };

  interface Terminal {
    exec(opt: TerminalExec): void;
  }

  const Terminal: Terminal;

  interface Window extends AndroidWindow<any> {}

  const Toast: {
    LENGTH_LONG: "long";
    LENGTH_SHORT: "short";
  };

  const WEB_BUILD_DATE: number;

  const __webpack__mode__: "production" | "development";

  interface StoredRepo extends Omit<Repo, "modules"> {
    modules: string;
  }

  interface Repo {
    /**
     * An required filed, to disply the repository name
     */
    name: string;
    mmrlOwner?: string;
    /**
     * An given website link for the repository
     */
    website?: string;
    /**
     * Given support link i.g. Telegram, Xda, GitHub or something
     */
    support?: string;
    donate?: string;
    submitModule?: string;
    last_update: number;
    modules: Module[];
  }

  export interface Root {
    last_update: number;
    name: string;
    website: any;
    support: any;
    donate: any;
    submitModule: string;
    modules: Module[];
  }

  export interface Module {
    id: string;
    name: string;
    version?: number;
    versionCode: number;
    author?: string;
    description?: string;
    valid: boolean;
    download: string;
    last_update: number;
    readme: string;
    stars: number;
    about: About;
    mmrl: Mmrl;
    fox: Fox;
  }

  export interface About {
    issues?: string;
    source: string;
  }

  export interface Mmrl {
    cover?: string;
    logo?: string;
    screenshots?: Array<string>;
    categories?: Array<string>;
  }

  export interface Fox {
    minApi?: number;
    maxApi?: number;
    minMagisk?: number;
    needRamdisk?: boolean;
    support?: string;
    donate?: string;
    config?: string;
    changeBoot?: boolean;
    mmtReborn?: boolean;
  }
}
