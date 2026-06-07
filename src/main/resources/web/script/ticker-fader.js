/*    Ticker Fader
 *
 * Adds a fade effect to ticker items so they slowly fade out after a while.
 */

/**
 * @param element element to fade
 *
 * requires a fadeDelay and fadeDuration in milliseconds in the global scope
 */
function delayedFade(element) {
    setTimeout(() => {
        let start = null;
        function animate(timestamp) {
            if (!start) start = timestamp;
            const progress = timestamp - start;
            element.style.opacity = Math.max(1 - progress / fadeDuration, 0);
            if (progress < fadeDuration) {
                window.requestAnimationFrame(animate);
            } else {
                element.style.opacity = 0;
                element.delete()
            }
        }
        window.requestAnimationFrame(animate);
    }, fadeDelay);
}

new MutationObserver((mutationsList, observer) => {
    for (const mutation of mutationsList) {
        mutation.addedNodes.forEach(node => {
            if (node.nodeType === Node.ELEMENT_NODE) {
                delayedFade(node);
            }
        });
    }
}).observe(document.getElementById('actionlist'), {
    childList: true,
    subtree: false
});
