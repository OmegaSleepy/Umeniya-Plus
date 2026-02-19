function createUserIsland() {
    const header_container = document.getElementById("header-container");
    if (!header_container) return;

    header_container.innerHTML = header_container.innerHTML + `
        <div class="userInformation" style="display: flex; align-items: center;">
            <label id="username" style="margin-right: 10px; display: none;">Потребител</label>
            <a href="/dashboard">
                <img id="profile-icon" src="/favicon-logo.ico" alt="Профил"
                     style="max-height: 35px; border-radius: 50%; border: 2px solid var(--primary-color);">
            </a>
        </div>`;
}

async function loadUserInformation() {
    try {
        const response = await fetch("/api/user/me-info");
        const userInformation = await response.json();

        const username = document.getElementById("username");
        const profileIcon = document.getElementById("profile-icon");

        if (username) {
            username.innerText = userInformation.username;
            username.style.display = "inline-block";
        }

        if (profileIcon) {
            profileIcon.src = "/api/profile-icon/" + userInformation.icon;
        }
    } catch (error) {
        console.error("Failed to load user info:", error);
    }
}

createUserIsland();
loadUserInformation();