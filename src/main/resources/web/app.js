const pluginsContainer = document.querySelector("#plugins-container");

const plugins = [
  {
    name: "Essentials",
    description: "Plugin principal",
    fileName: "Essentials.jar",
  },

  {
    name: "Economy",
    description: "Gestion économie",
    fileName: "Economy.jar",
  },

  {
    name: "Teleport",
    description: "Plugin téléportation",
    fileName: "Teleport.jar",
  },
];

function displayPlugins(plugins) {
  pluginsContainer.innerHTML = "";

  plugins.forEach((plugin) => {
    const pluginCard = document.createElement("div");

    pluginCard.classList.add("plugin-card");

    pluginCard.innerHTML = `
      <span>
        ${plugin.fileName}
      </span>

      <div class="actions">
        <button class="update-btn action">
          Update
        </button>

        <button class="delete-btn action">
          Delete
        </button>
      </div>
    `;

    pluginsContainer.appendChild(pluginCard);
  });
}

displayPlugins(plugins);
