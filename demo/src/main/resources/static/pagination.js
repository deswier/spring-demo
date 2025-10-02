/* ---------- Pagination UI ---------- */
// pagination.js — standalone, React 17 compatible
(function () {
  const h = React.createElement;

  function Pagination(props){
    var page = props.page,
        totalPages = props.totalPages,
        pageSize = props.pageSize,
        totalElements = props.totalElements,
        t = props.t;

    function startIndex(){ return totalElements === 0 ? 0 : page * pageSize + 1; }
    function endIndex(){ var end = (page + 1) * pageSize; return Math.min(end, totalElements); }

    function pageButton(label, disabled, onClick, isCurrent){
      var base = {
        padding: '6px 10px',
        borderRadius: '8px',
        border: '1px solid var(--border)',
        background: isCurrent ? '#eef2ff' : '#fff',
        color: isCurrent ? '#1f2937' : '#374151',
        cursor: disabled ? 'not-allowed' : 'pointer',
        opacity: disabled ? 0.6 : 1,
        fontWeight: isCurrent ? 700 : 500
      };
      return h('button', { style: base, disabled: disabled, onClick: onClick }, label);
    }

    function ellipsis(){ return h('span', { style: { padding: '0 6px', color: '#9ca3af' } }, '…'); }

    function buildPageNumbers(){
      var btns = [];
      if (totalPages <= 1) return btns;
      var first = 0, last = totalPages - 1, curr = page;
      function goto(i){ return function(){ props.onGoto(i); }; }
      btns.push(pageButton('1', false, goto(first), curr === first));
      if (curr - 2 > first) btns.push(ellipsis());
      var start = Math.max(first + 1, curr - 1);
      var end = Math.min(last - 1, curr + 1);
      for (var i = start; i <= end; i++){
        if (i > first && i < last) btns.push(pageButton(String(i + 1), false, goto(i), curr === i));
      }
      if (curr + 2 < last) btns.push(ellipsis());
      if (last > first) btns.push(pageButton(String(last + 1), false, goto(last), curr === last));
      return btns;
    }

    var barStyle = { display: 'flex', alignItems: 'center', gap: '16px', padding: '12px 0', flexWrap: 'wrap', justifyContent: 'space-between' };

    var left = h('div', { style: { display: 'flex', alignItems: 'center', gap: '6px', color: '#6b7280', fontSize: '14px' } },
      h('select', {
        value: pageSize,
        onChange: function(ev){ props.onPageSize(parseInt(ev.target.value, 10)); },
        style: { border: '1px solid var(--border)', borderRadius: '8px', padding: '6px 8px', background: '#fff', cursor: 'pointer' }
      }, [5,10,20,50].map(function(n){ return h('option', { key: n, value: n }, n + ' ' + t('pagination.entries')); }))
    );

    var middle = h('div', { style: { color: '#6b7280', fontSize: '14px', flex: 1 } },
      t('pagination.showing'), ' ', startIndex(), ' ', t('pagination.to'), ' ', endIndex(), ' ', t('pagination.of'), ' ', totalElements, ' ', t('pagination.entries'), '.'
    );

    var right = h('div', { style: { display: 'flex', alignItems: 'center', gap: '6px' } },
      pageButton('‹', page <= 0, props.onPrev, false),
      buildPageNumbers(),
      pageButton('›', page + 1 >= totalPages, props.onNext, false)
    );

    return h('div', { style: barStyle }, left, middle, right);
  }

  // export
  window.Pagination = Pagination;
})();
