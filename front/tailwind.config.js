module.exports = {
  content: ['./src/**/*.{html,ts}'],
  theme: { extend: {} },
  plugins: [],
  corePlugins: { preflight: false },   // <— évite les resets qui cassent PrimeNG
};