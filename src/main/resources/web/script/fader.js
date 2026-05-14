/*    Ticker Fader
 *
 * Adds a fade effect to ticker items so they slowly fade out after a while.
 */

/**
 * @param element element to fade
 * @param delay delay before the fade starts in milliseconds
 * @param duration duration of the fade in milliseconds
 */
function delayedFade(element, delay, duration) {
    setTimeout(() => {
        let start = null;
        function animate(timestamp) {
            if (!start) start = timestamp;
            const progress = timestamp - start;
            element.style.opacity = Math.max(1 - progress / duration, 0);
            if (progress < duration) {
                window.requestAnimationFrame(animate);
            } else {
                element.style.opacity = 0;
                element.delete()
            }
        }
        window.requestAnimationFrame(animate);
    }, delay);
}
