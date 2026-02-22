(function() {
    const path = window.location.pathname;
    const hasToken = document.cookie.includes('token') || localStorage.getItem('token');
    let cssUrl = null;

    const isProfilePage = path.match(/^\/user\/([^\/]+)/);
    const isBlogPage = path.startsWith("/blog/");

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
        setTimeout(showPage, 400);

        document.head.appendChild(themeLink);
    };

    if (isProfilePage) {
        applyStyles(`/api/style-for-someone/${isProfilePage[1]}`);
    }
    else if (isBlogPage) {
        const authorCheck = setInterval(() => {
            const authorEl = document.getElementById("post-author");
            if (authorEl) {
                clearInterval(authorCheck);
                const username = authorEl.textContent.trim();
                applyStyles(`/api/style-for-someone/${username}`);
            }
        }, 10);

        setTimeout(() => clearInterval(authorCheck), 1000);
    }
    else if (hasToken) {
        applyStyles("/api/style-for/me");
    }
})();