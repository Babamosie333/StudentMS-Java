import { Html, Head, Main, NextScript } from "next/document";

export default function Document() {
  return (
    <Html lang="en">
      <Head>
        <link rel="icon" href="/favicon.ico" />
        <link rel="icon" type="image/png" sizes="512x512" href="/icon.png" />

        <title>Student Management System</title>
        <meta
          name="description"
          content="A Java + MongoDB Atlas Student Management System, with desktop and web interfaces."
        />

        {/* Open Graph */}
        <meta property="og:title" content="Student Management System" />
        <meta
          property="og:description"
          content="A Java + MongoDB Atlas Student Management System, with desktop and web interfaces."
        />
        <meta property="og:image" content="/og-image.png" />
        <meta property="og:type" content="website" />

        {/* Twitter card (uses same OG image) */}
        <meta name="twitter:card" content="summary_large_image" />
        <meta name="twitter:title" content="Student Management System" />
        <meta
          name="twitter:description"
          content="A Java + MongoDB Atlas Student Management System, with desktop and web interfaces."
        />
        <meta name="twitter:image" content="/og-image.png" />
      </Head>
      <body>
        <Main />
        <NextScript />
      </body>
    </Html>
  );
}