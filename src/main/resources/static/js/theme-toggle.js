document.addEventListener("DOMContentLoaded", function () {
    const storageKey = "digilib-theme";
    const rootElement = document.documentElement;
    const toggleButtons = document.querySelectorAll(".theme-toggle");

    const savedTheme = localStorage.getItem(storageKey);
    if (savedTheme === "dark" || savedTheme === "light") {
        rootElement.setAttribute("data-theme", savedTheme);
    }

    function applyButtonText() {
        const theme = rootElement.getAttribute("data-theme") === "dark" ? "dark" : "light";
        toggleButtons.forEach(function (button) {
            button.textContent = theme === "dark" ? "Switch to Light" : "Switch to Dark";
        });
    }

    toggleButtons.forEach(function (button) {
        button.addEventListener("click", function () {
            const currentTheme = rootElement.getAttribute("data-theme") === "dark" ? "dark" : "light";
            const nextTheme = currentTheme === "dark" ? "light" : "dark";
            rootElement.setAttribute("data-theme", nextTheme);
            localStorage.setItem(storageKey, nextTheme);
            applyButtonText();
        });
    });

    applyButtonText();
});
