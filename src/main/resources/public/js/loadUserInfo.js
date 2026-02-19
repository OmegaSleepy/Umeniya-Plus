function createUserIsland() {
    const header_container = document.getElementById("header-container");
    if (!header_container) return;

    header_container.insertAdjacentHTML('beforeend', `
    <div class="user-island" style="display: flex; align-items: center; margin-left: auto; gap: 12px;">

        <div class="flames-badge" id="badge" style="
            display: flex; 
            align-items: center; 
            background: linear-gradient(135deg, rgba(255, 145, 0, 0.1), rgba(255, 81, 0, 0.1));
            padding: 4px 10px; 
            border-radius: 20px; 
            border: 1px solid rgba(255, 81, 0, 0.3);
            box-shadow: 0 2px 4px rgba(0,0,0,0.05);">
            
            <img id="flames" src="/api/image/favicon.ico" alt="Искри" style="
                max-height: 20px; 
                filter: drop-shadow(0 0 2px rgba(255, 81, 0, 0.5));
                margin-right: 6px;">
            
            <span id="flames-count" style="
                font-weight: 800; 
                color: #ff5100; 
                font-style: normal;
                font-size: 0.95rem;">0</span>
        </div>
        
        <label id="username" style="display: none; font-weight: 600; color: var(--text-dark); font-size: 0.9rem;">
            Потребител
        </label>

        <a href="/dashboard" style="display: flex; align-items: center; transition: transform 0.2s ease;" 
           onmouseover="this.style.transform='scale(1.05)'" 
           onmouseout="this.style.transform='scale(1)'">
            <img id="profile-icon" src="/favicon-logo.ico" alt="Профил" style="
                max-height: 38px; 
                width: 38px;
                object-fit: cover;
                border-radius: 50%; 
                border: 2px solid var(--primary-color);
                box-shadow: var(--text-main);">
        </a>
    </div>`);
}

async function loadUserInformation() {
    const iskri = document.getElementById("badge");
    iskri.style.display = "none";

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
        iskri.style.display = "inline-block";
        setVisualFlames(userInformation.flames);

    } catch (error) {

        console.error("Failed to load user info:", error);
    }
}

function setVisualFlames(i){
    const flames = document.getElementById("flames-count");
    flames.innerText = i;
}

createUserIsland();
loadUserInformation();