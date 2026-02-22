(function() {
    const path = window.location.pathname;
    let cssUrl = "/api/style-for/me";

    const userMatch = path.match(/^\/user\/([^\/]+)/);
    if (userMatch) {
        cssUrl = `/api/style-for-someone/${userMatch[1]}`;
    }

    const themeLink = document.createElement("link");
    themeLink.rel = "stylesheet";
    themeLink.href = cssUrl;
    themeLink.id = "user-custom-theme";

    if (!path.includes('shop')) {
        document.documentElement.style.visibility = 'hidden';
        themeLink.onload = () => document.documentElement.style.visibility = 'visible';
        setTimeout(() => document.documentElement.style.visibility = 'visible', 500);
    }

    document.head.appendChild(themeLink);
})();