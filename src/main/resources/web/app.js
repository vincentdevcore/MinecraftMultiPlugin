const uploadForm = document.getElementById("uploadForm");
const fileInput = document.getElementById("fileInput");
const fileInfo = document.getElementById("fileInfo");
const submitBtn = document.getElementById("submitBtn");
const progressFill = document.getElementById("progressFill");
const progressText = document.getElementById("progressText");
const statusBox = document.getElementById("statusBox");

fileInput.addEventListener("change", () => {
    const file = fileInput.files[0];
    if (!file) {
        fileInfo.textContent = "Aucun fichier sélectionné.";
        return;
    }

    fileInfo.textContent = `${file.name} (${formatBytes(file.size)})`;
});

uploadForm.addEventListener("submit", (event) => {
    event.preventDefault();

    const file = fileInput.files[0];
    if (!file) {
        showStatus("error", "Veuillez sélectionner un fichier .jar.");
        return;
    }

    if (!file.name.toLowerCase().endsWith(".jar")) {
        showStatus("error", "Seuls les fichiers .jar sont autorisés.");
        return;
    }

    const formData = new FormData();
    formData.append("file", file);

    submitBtn.disabled = true;
    setProgress(0);
    hideStatus();

    const xhr = new XMLHttpRequest();
    xhr.open("POST", "/api/upload");

    xhr.upload.addEventListener("progress", (event) => {
        if (event.lengthComputable) {
            const percent = Math.round((event.loaded / event.total) * 100);
            setProgress(percent);
        }
    });

    xhr.onload = () => {
        submitBtn.disabled = false;

        let response = {};
        try {
            response = JSON.parse(xhr.responseText);
        } catch (e) {
            showStatus("error", "Réponse serveur invalide.");
            return;
        }

        if (xhr.status >= 200 && xhr.status < 300 && response.success) {
            showStatus("success", `Upload réussi : ${response.file}`);
            setProgress(100);
        } else {
            showStatus("error", response.message || "Échec de l'upload.");
        }
    };

    xhr.onerror = () => {
        submitBtn.disabled = false;
        showStatus("error", "Impossible de contacter le serveur.");
    };

    xhr.send(formData);
});

function setProgress(percent) {
    progressFill.style.width = `${percent}%`;
    progressText.textContent = `${percent}%`;
}

function showStatus(type, message) {
    statusBox.className = `status ${type}`;
    statusBox.textContent = message;
}

function hideStatus() {
    statusBox.className = "status hidden";
    statusBox.textContent = "";
}

function formatBytes(bytes) {
    if (bytes < 1024) {
        return `${bytes} B`;
    }
    if (bytes < 1024 * 1024) {
        return `${(bytes / 1024).toFixed(2)} KB`;
    }
    return `${(bytes / (1024 * 1024)).toFixed(2)} MB`;
}