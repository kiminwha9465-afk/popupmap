document.addEventListener('click', e => {
    document.querySelectorAll('details.nav-dropdown[open]').forEach(d => {
        if (!d.contains(e.target)) d.removeAttribute('open');
    });
    var bellDd = document.getElementById('bellDropdown');
    if (bellDd && !bellDd.hidden) {
        var bellBtn = document.getElementById('bellBtn');
        if (!bellBtn || (!bellBtn.contains(e.target) && !bellDd.contains(e.target))) {
            bellDd.hidden = true;
        }
    }
});

(function () {
    var bellBtn = document.getElementById('bellBtn');
    var bellDd  = document.getElementById('bellDropdown');
    if (!bellBtn || !bellDd) return;
    bellBtn.addEventListener('click', function (e) {
        e.stopPropagation();
        bellDd.hidden = !bellDd.hidden;
    });
})();

document.querySelectorAll('details.nav-dropdown').forEach(d => {
    d.addEventListener('toggle', () => {
        if (!d.open) return;
        const summary = d.querySelector('summary');
        const menu = d.querySelector('.nav-dropdown-menu');
        if (!menu || !summary) return;
        const r = summary.getBoundingClientRect();
        menu.style.top = (r.bottom + 4) + 'px';
        menu.style.left = r.left + 'px';
    });
});
