(function ($) {
    'use strict';

    var $body = $('body');
    var $sidebar = $('#sidebar');
    var $toggle = $('#appSidebarToggle');
    var $backdrop = $('#appSidebarBackdrop');
    var $close = $('#appSidebarClose');
    var mobileQuery = window.matchMedia('(max-width: 991px)');

    function setSidebarOpen(open) {
        var shouldOpen = Boolean(open && mobileQuery.matches);
        $sidebar.toggleClass('is-open', shouldOpen);
        $backdrop.toggleClass('is-open', shouldOpen);
        $body.toggleClass('sidebar-open', shouldOpen);
        $toggle.attr('aria-expanded', String(shouldOpen));
        $toggle.attr('aria-label', shouldOpen ? 'Đóng menu điều hướng' : 'Mở menu điều hướng');
        $sidebar.attr('aria-hidden', String(mobileQuery.matches && !shouldOpen));

        if (shouldOpen) {
            window.setTimeout(function () {
                $close.trigger('focus');
            }, 510);
        }
    }

    function syncViewport() {
        if (!mobileQuery.matches) {
            setSidebarOpen(false);
            $sidebar.attr('aria-hidden', 'false');
        } else if (!$sidebar.hasClass('is-open')) {
            $sidebar.attr('aria-hidden', 'true');
        }
    }

    function setHeaderTitle() {
        var title = document.title.split('|')[0].trim();
        if (title) {
            $('#appHeaderTitle').text(title);
        }
    }

    $(function () {
        setHeaderTitle();
        syncViewport();

        $toggle.on('click', function () {
            setSidebarOpen(!$sidebar.hasClass('is-open'));
        });
        $backdrop.add($close).on('click', function () {
            setSidebarOpen(false);
            $toggle.trigger('focus');
        });
        $sidebar.on('click', 'a', function () {
            if (mobileQuery.matches) {
                setSidebarOpen(false);
            }
        });
        $(document).on('keydown', function (event) {
            if (event.key === 'Escape' && $sidebar.hasClass('is-open')) {
                setSidebarOpen(false);
                $toggle.trigger('focus');
            }
        });

        $('[data-toggle="tooltip"]').tooltip({container: 'body'});
    });

    if (typeof mobileQuery.addEventListener === 'function') {
        mobileQuery.addEventListener('change', syncViewport);
    } else {
        mobileQuery.addListener(syncViewport);
    }
})(jQuery);
