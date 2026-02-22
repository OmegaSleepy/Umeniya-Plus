// 1. Elements
const contentInput = document.getElementById('content');
const titleInput = document.getElementById('title');
const excerptInput = document.getElementById('excerpt');
const categoryInput = document.getElementById('category');
const previewPanel = document.getElementById('preview-panel');
const toggleBtn = document.getElementById('togglePreview');
const blogForm = document.getElementById('editBlogForm') || document.getElementById('blogForm');

// 2. Identify Mode (Edit vs Create)
const pathParts = window.location.pathname.split('/');
const isEditMode = pathParts.includes('edit');
const blogId = isEditMode ? pathParts[pathParts.length - 1] : null;

// 3. UI Logic (Preview)
function updatePreview() {
    document.getElementById('preview-title').textContent = titleInput.value || "Преглед на заглавието";
    document.getElementById('preview-excerpt').textContent = excerptInput.value || "Краткото описание ще се появи тук...";

    const tag = document.getElementById('preview-tag');
    tag.textContent = categoryInput.value || "Категория";
    // Add dynamic class for styling if it exists
    tag.className = 'post-tag ' + (categoryInput.value || '').toLowerCase().replace(/\s+/g, '-');

    const rawValue = contentInput.value;
    if (window.marked) {
        document.getElementById('markdown-body').innerHTML = marked.parse(rawValue);
    }
}

// Toggle Preview Visibility
toggleBtn.addEventListener('click', () => {
    previewPanel.classList.toggle('active');
    const isShowing = previewPanel.classList.contains('active') || previewPanel.style.display === 'block';
    previewPanel.style.display = isShowing ? 'block' : 'none';
    if (isShowing) updatePreview();
});

// Real-time Preview Listeners
[contentInput, titleInput, excerptInput, categoryInput].forEach(el => {
    el.addEventListener('input', updatePreview);
});

// 4. Data Loading Logic
async function initForm() {
    try {
        // Always load categories/tags
        const catRes = await fetch("/api/blog/tags");
        if (!catRes.ok) throw new Error("Failed to fetch tags");
        const tags = await catRes.json();

        categoryInput.innerHTML = "";
        Object.keys(tags).forEach(key => {
            const option = document.createElement("option");
            option.value = key;
            option.textContent = key;
            categoryInput.appendChild(option);
        });

        // If Editing: Fetch existing blog content
        if (isEditMode && blogId) {
            const blogRes = await fetch(`/api/blog/full/${blogId}`);
            if (!blogRes.ok) throw new Error("Blog post not found");
            const blog = await blogRes.json();

            titleInput.value = blog.title || "";
            excerptInput.value = blog.excerpt || "";
            contentInput.value = blog.content || "";
            categoryInput.value = blog.category || "";

            updatePreview();
        } else {
            // Default selection for "Create" mode
            categoryInput.selectedIndex = Math.max(0, categoryInput.options.length - 2);
        }

    } catch (err) {
        console.error("Initialization error:", err);
        if (isEditMode) {
            Swal.fire({ title: 'Грешка!', text: 'Неуспешно зареждане на данните.', icon: 'error' });
        }
    }
}

// 5. Submit Logic (Unified)
blogForm.addEventListener("submit", async (event) => {
    event.preventDefault();

    const data = {
        title: titleInput.value,
        category: categoryInput.value,
        excerpt: excerptInput.value,
        content: contentInput.value
    };

    const config = isEditMode ? {
        url: `/api/blog/update/${blogId}`,
        method: "PUT",
        successMsg: "Публикацията беше редактирана успешно!",
        redirect: `/blog/${blogId}`
    } : {
        url: "/api/blog/create",
        method: "POST",
        successMsg: "Публикацията е успешно публикувана!",
        redirect: "/home"
    };

    try {
        const response = await fetch(config.url, {
            method: config.method,
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(data)
        });

        if (!response.ok) throw new Error("Submission failed");

        await Swal.fire({
            title: 'Успех!',
            text: config.successMsg,
            icon: 'success',
            confirmButtonColor: '#ff4500',
        });
        window.location.href = config.redirect;

    } catch (err) {
        console.error("Submission error:", err);
        Swal.fire({
            title: 'Грешка!',
            text: 'Случи се случка, опитайте отново...',
            icon: 'error',
            confirmButtonColor: '#3085d6'
        });
    }
});

// Character Counter Logic
function updateCounter(inputId, counterId, max) {
    const input = document.getElementById(inputId);
    const counter = document.getElementById(counterId);
    input.addEventListener('input', () => {
        counter.innerText = `${input.value.length} / ${max}`;
        if (input.value.length >= max) {
            counter.style.color = 'var(--tetra-2)';
        } else {
            counter.style.color = 'var(--text-dark)';
        }
    });
}

updateCounter('title', 'titleCount', 64);
updateCounter('excerpt', 'excerptCount', 128);
updateCounter('content', 'contentCount', 8000);

// Validation before submitting
try {
    document.getElementById('blogForm').addEventListener('submit', function(e) {
        const title = document.getElementById('title').value;
        const excerpt = document.getElementById('excerpt').value;
        const content = document.getElementById('content').value;

        if (title.length >= 65 || excerpt.length >= 129 || content.length >= 8001) {
            e.preventDefault();
            Swal.fire({
                icon: 'error',
                title: 'Твърде дълъг текст!',
                text: 'Моля, спазвайте ограниченията за брой символи.',
                confirmButtonColor: '#FF4D6D'
            });
        }
    });
} catch (err) {
    document.getElementById('editBlogForm').addEventListener('submit', function(e) {
        const title = document.getElementById('title').value;
        const excerpt = document.getElementById('excerpt').value;
        const content = document.getElementById('content').value;

        if (title.length >= 65 || excerpt.length >= 129 || content.length >= 8001) {
            e.preventDefault();
            Swal.fire({
                icon: 'error',
                title: 'Твърде дълъг текст!',
                text: 'Моля, спазвайте ограниченията за брой символи.',
                confirmButtonColor: '#FF4D6D'
            });
        }
    });
}

function counter(e){

}


// Run
initForm();