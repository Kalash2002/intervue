// Vite returns a file's text for a "?raw" import; the layout test reads the room stylesheet this way.
declare module '*.css?raw' {
  const text: string;
  export default text;
}
