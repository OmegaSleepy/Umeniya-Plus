(function() {
    const path = window.location.pathname;
    const hasToken = document.cookie.includes('token') || localStorage.getItem('token');

    const applyStyles = (url) => {
        if (!url || document.getElementById("user-custom-theme")) return;

        document.documentElement.style.visibility = 'hidden';
        const themeLink = document.createElement("link");
        themeLink.rel = "stylesheet";
        themeLink.href = url;
        themeLink.id = "user-custom-theme";

        const showPage = () => document.documentElement.style.visibility = 'visible';
        themeLink.onload = showPage;
        themeLink.onerror = showPage;
        setTimeout(showPage, 500);

        document.head.appendChild(themeLink);
    };

    // --- 1. SOMEONE ELSE'S THEME (Highest Priority) ---

    const profileMatch = path.match(/^\/user\/([^\/]+)/);
    if (profileMatch) {
        return applyStyles(`/api/style-for-someone/${profileMatch[1]}`);
    }

    if (path.startsWith("/blog/")) {
        const authorCheck = setInterval(() => {
            const authorEl = document.getElementById("post-author");
            if (authorEl) {
                clearInterval(authorCheck);
                applyStyles(`/api/style-for-someone/${authorEl.textContent.trim()}`);
            }
        }, 10);
        setTimeout(() => clearInterval(authorCheck), 1000);
        return;
    }

    // --- 2. YOUR THEME (Me) ---
    const isMyThemePage = path === "/" ||
        path === "/home" ||
        path.startsWith("/help") ||
        path.startsWith("/dashboard") ||
        path.startsWith("/shop") ||
        path.startsWith("/edit") ||
        path.startsWith("/create");

    if (isMyThemePage) {
        applyStyles("/api/style-for/me");
    }
})();