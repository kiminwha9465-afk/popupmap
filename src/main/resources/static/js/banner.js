(() => {
    const track  = document.getElementById('bannerTrack');
    const prev   = document.getElementById('bannerPrev');
    const next   = document.getElementById('bannerNext');
    const dotsEl = document.getElementById('bannerDots');

    if (!track) return;

    const slides = track.querySelectorAll('.banner-slide');
    const total  = slides.length;
    if (total === 0) return;

    let current = 0;
    let timer;

    // 점(dot) 생성
    slides.forEach((_, i) => {
        const dot = document.createElement('button');
        dot.className = 'banner-dot' + (i === 0 ? ' active' : '');
        dot.setAttribute('aria-label', `슬라이드 ${i + 1}`);
        dot.addEventListener('click', () => goTo(i));
        dotsEl.appendChild(dot);
    });

    function goTo(idx) {
        current = (idx + total) % total;
        track.style.transform = `translateX(-${current * 100}%)`;
        dotsEl.querySelectorAll('.banner-dot').forEach((d, i) => {
            d.classList.toggle('active', i === current);
        });
        resetTimer();
    }

    function resetTimer() {
        clearInterval(timer);
        timer = setInterval(() => goTo(current + 1), 4500);
    }

    prev.addEventListener('click', () => goTo(current - 1));
    next.addEventListener('click', () => goTo(current + 1));

    // 터치 스와이프
    let startX = 0;
    track.addEventListener('touchstart', e => { startX = e.touches[0].clientX; }, { passive: true });
    track.addEventListener('touchend',   e => {
        const dx = e.changedTouches[0].clientX - startX;
        if (Math.abs(dx) > 50) goTo(dx < 0 ? current + 1 : current - 1);
    });

    resetTimer();
})();
