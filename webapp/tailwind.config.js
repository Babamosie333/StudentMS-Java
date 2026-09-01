/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./pages/**/*.{js,jsx}",
    "./components/**/*.{js,jsx}",
  ],
  theme: {
    extend: {
      colors: {
        background: "#F5F7FA",
        card: "#FFFFFF",
        text: "#222733",
        subtext: "#6B7280",
        border: "#E1E5EA",
        primary: "#4A6CF2",
        primaryDark: "#3753C9",
        danger: "#E04F4F",
        success: "#2EA56A",
        warning: "#E09A2E",
      },
      fontFamily: {
        sans: ["Segoe UI", "system-ui", "sans-serif"],
      },
      borderRadius: {
        xl: "0.75rem",
        "2xl": "1rem",
      },
    },
  },
  plugins: [],
};
