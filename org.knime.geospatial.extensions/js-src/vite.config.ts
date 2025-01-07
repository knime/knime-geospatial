import { URL, fileURLToPath } from "node:url";

import { defineConfig } from "vitest/config";

// https://vitest.dev/config/
export default defineConfig(({ mode }) => ({
  base: "./",
  build: {
    emptyOutDir: false,
    cssCodeSplit: false,
    rollupOptions: {
      input: fileURLToPath(new URL(`./${mode}.html`, import.meta.url)),
    },
  },
  test: {
    include: ["src/**/__tests__/**/*.test.{js,mjs,cjs,ts,mts,cts,jsx,tsx}"],
    environment: "jsdom",
    reporters: ["default", "junit"],
    coverage: {
      all: true,
      exclude: [
        "buildtools/",
        "coverage/**",
        "dist/**",
        "lib/**",
        "**/*.d.ts",
        "**/__tests__/**",
        "test-setup/**",
        "**/{vite,vitest,postcss,lint-staged}.config.{js,cjs,mjs,ts}",
        "**/.{eslint,prettier,stylelint}rc.{js,cjs,yml}",
        "**/types/**",
        "**/dev/**",
      ],
      reporter: ["html", "text", "lcov"],
      reportsDirectory: "coverage",
    },
    outputFile: {
      junit: "test-results/junit.xml", // needed for Bitbucket Pipeline, see https://support.atlassian.com/bitbucket-cloud/docs/test-reporting-in-pipelines/
    },
  },
  envPrefix: "KNIME_",
}));
