const js = require("@eslint/js");
const globals = require("globals");
module.exports = [
  js.configs.recommended,
  {
    files: ["src/main/resources/static/**/*.js"],
    languageOptions: { ecmaVersion: 2022, sourceType: "script", globals: { ...globals.browser, Chart: "readonly" } },
    rules: { "no-unused-vars": "warn", "no-redeclare": ["error", { builtinGlobals: false }] }
  }
];
