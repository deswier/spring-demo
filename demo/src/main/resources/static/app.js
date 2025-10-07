const h = React.createElement;

/* ---------- Reusable components ---------- */

function InputField(props){
  var hasErr = !!props.error;
  return h('div', { className: 'field' },
    h('label', { className: 'label' }, props.label),
    h('input', {
      className: 'input ' + (hasErr ? 'input-error' : ''),
      type: props.type || 'text',
      placeholder: props.placeholder || '',
      value: props.value,
      onChange: function(ev){ props.onChange(ev.target.value); }
    }),
    h('div', { className: 'error-slot ' + (hasErr ? '' : 'hidden') }, hasErr ? props.error : 'placeholder')
  );
}

function KebabMenu(props){
  if (!props.open) return null;
  return h('div', { className: 'menu' },
    h('button', { className: 'menu-item', onClick: props.onEdit }, props.t('student.table.menu.edit')),
    h('button', { className: 'menu-item menu-danger', onClick: props.onDelete }, props.t('student.table.menu.delete'))
  );
}

function StudentsTable(props){
  return h('table', null,
    h('thead', null,
      h('tr', null,
        h('th', null, props.t('student.table.header.name')),
        h('th', null, props.t('student.table.header.email')),
        h('th', null, props.t('student.table.header.dob')),
        h('th', { style: { width: '80px', textAlign: 'right' } }, '')
      )
    ),
    h('tbody', null,
      (props.students || []).map(function(s){
        var open = props.menuOpenId === s.id;
        return h('tr', { key: s.id },
          h('td', null, s.name || ''),
          h('td', null, s.email || ''),
          h('td', null, (s.dob || '').slice(0,10)),
          h('td', { className: 'actions' },
            h('button', { className: 'kebab', onClick: function(){ props.onToggleMenu(s.id); } }, '⋮'),
            h(KebabMenu, {
              open: open,
              onEdit: function(){ props.onEdit(s); },
              onDelete: function(){ props.onDelete(s.id); },
              t: props.t
            })
          )
        );
      })
    )
  );
}

/* ---------- App ---------- */

const App = () => {
  const [students, setStudents] = React.useState([]);
  const [messages, setMessages] = React.useState({});

  const [name, setName] = React.useState('');
  const [email, setEmail] = React.useState('');
  const [dob, setDob] = React.useState(''); // yyyy-MM-dd

  const [editingId, setEditingId] = React.useState(null);
  const [menuOpenId, setMenuOpenId] = React.useState(null);

  const [errors, setErrors] = React.useState({ name: '', email: '', dob: '', global: '' });

  // pagination
  const [page, setPage] = React.useState(0);       // 0-based
  const [pageSize, setPageSize] = React.useState(10);
  const [totalPages, setTotalPages] = React.useState(0);
  const [totalElements, setTotalElements] = React.useState(0);

  // NEW: track if user is authorized; show Logout button when true
  const [authorized, setAuthorized] = React.useState(false);

  const lang = new URLSearchParams(window.location.search).get('lang') || 'ru';

  React.useEffect(function(){
    fetch('/api/v1/messages?lang=' + lang)
      .then(function(r){ return r.json(); })
      .then(setMessages)
      .catch(function(){});
  }, [lang]);

  const t = React.useCallback(function(key){
    return messages[key] || key;
  }, [messages]);

  const loadStudents = React.useCallback(function(){
    fetch('/api/v1/student/students?page=' + page + '&pageSize=' + pageSize)
      .then(async function(r){
        if (r.status === 401) {
          setAuthorized(false);
          return { content: [], totalPages: 0, totalElements: 0 };
        }
        setAuthorized(true);
        return r.json();
      })
      .then(function(data){
        setStudents(Array.isArray(data.content) ? data.content : []);
        setTotalPages(typeof data.totalPages === 'number' ? data.totalPages : 0);
        setTotalElements(typeof data.totalElements === 'number' ? data.totalElements : (Array.isArray(data.content) ? data.content.length : 0));
      })
      .catch(function(){});
  }, [page, pageSize]);

  React.useEffect(function(){ loadStudents(); }, [loadStudents]);

  function hasFieldErrors(obj){ return !!(obj && (obj.name || obj.email || obj.dob)); }

  function mapApiErrorsToState(apiError) {
    var next = { name: '', email: '', dob: '', global: '' };
    if (apiError && Array.isArray(apiError.fields)) {
      apiError.fields.forEach(function(f){
        var field = (f && f.field ? f.field : '').toLowerCase();
        if (field && Object.prototype.hasOwnProperty.call(next, field)) {
          next[field] = (f && f.message) ? f.message : t('error.invalid.value');
        }
      });
    }
    var msg = apiError && apiError.message ? apiError.message : null;
    next.global = hasFieldErrors(next) ? '' : (msg || t('error.request.failed'));
    return next;
  }

  function safeJson(res){ return res.json().catch(function(){ return null; }); }

  async function handleCreate(){
    setErrors({ name: '', email: '', dob: '', global: '' });
    var payload = { name: name, email: email, dob: dob };
    var res = await fetch('/api/v1/student', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    });
    if (!res.ok){
      var body = await safeJson(res);
      setErrors(mapApiErrorsToState(body));
      return;
    }
    await loadStudents();
    setName(''); setEmail(''); setDob('');
  }

  async function handleUpdate(){
    if (!editingId) return;
    setErrors({ name: '', email: '', dob: '', global: '' });
    var url = '/api/v1/student/' + editingId +
              '?name=' + encodeURIComponent(name) +
              '&email=' + encodeURIComponent(email);
    var res = await fetch(url, { method: 'PUT' });
    if (!res.ok){
      var body = await safeJson(res);
      setErrors(mapApiErrorsToState(body));
      return;
    }
    await loadStudents();
    setEditingId(null);
    setName(''); setEmail(''); setDob('');
  }

  async function handleDelete(id){
    await fetch('/api/v1/student/' + id, { method: 'DELETE' });
    await loadStudents();
    if (menuOpenId === id) setMenuOpenId(null);
  }

  function handleEditRow(s){
    setEditingId(s.id);
    setName(s.name || '');
    setEmail(s.email || '');
    setDob((s.dob || '').slice(0,10));
    setMenuOpenId(null);
    setErrors({ name: '', email: '', dob: '', global: '' });
  }

  function toggleMenu(id){ setMenuOpenId(function(curr){ return curr === id ? null : id; }); }

  // Banner (only when no field errors)
  var bannerNode = document.getElementById('banner');
  if (bannerNode){
    bannerNode.innerHTML = '';
    if (!hasFieldErrors(errors) && errors.global){
      var div = document.createElement('div');
      div.className = 'alert';
      div.textContent = errors.global;
      bannerNode.appendChild(div);
    }
  }

  return h('div', null,
    // --- Logout button (top-right, only when authorized) ---
    authorized && h('div', {
      style: {
        position: 'fixed',
        top: 12,
        right: 12,
        zIndex: 1000
      }
    },
      h('a', {
        href: 'http://localhost:8080/logout',
        style: {
          display: 'inline-block',
          padding: '8px 12px',
          border: '1px solid #e5e7eb',
          borderRadius: '10px',
          background: '#ffffff',
          textDecoration: 'none',
          fontWeight: 600
        }
      }, t('auth.logout'))
    ),

    // Form
    h('div', { className: 'form-grid' },
      h(InputField, {
        label: t('student.form.label.name'),
        value: name,
        onChange: function(v){ setName(v); setErrors(function(p){ return Object.assign({}, p, { name: '' }); }); },
        error: errors.name
      }),
      h(InputField, {
        label: t('student.form.label.email'),
        value: email,
        onChange: function(v){ setEmail(v); setErrors(function(p){ return Object.assign({}, p, { email: '' }); }); },
        type: 'email',
        error: errors.email
      }),
      h(InputField, {
        label: t('student.form.label.dob'),
        value: dob,
        onChange: function(v){ setDob(v); setErrors(function(p){ return Object.assign({}, p, { dob: '' }); }); },
        type: 'date',
        error: errors.dob
      }),
      h('div', null,
        h('button', { className: 'btn', onClick: editingId ? handleUpdate : handleCreate },
          editingId ? t('student.form.button.update') : t('student.form.button.add')
        )
      )
    ),

    // Table in second card
   h(TableMountHelper, {
     students: students,
     menuOpenId: menuOpenId,
     onToggleMenu: toggleMenu,
     onEdit: handleEditRow,
     onDelete: handleDelete,
     t: t,

     // pagination props routed to the helper
     page: page,
     totalPages: totalPages,
     pageSize: pageSize,
     totalElements: totalElements,
     onPrev: function(){ if (page > 0) setPage(page - 1); },
     onNext: function(){ if (page + 1 < totalPages) setPage(page + 1); },
     onGoto: function(i){ if (i >= 0 && i < totalPages) setPage(i); },
     onPageSize: function(n){ setPageSize(n); setPage(0); }
   })
  );
};

/* Mount helper renders the table AND the pager into the second card */
function TableMountHelper(props){
  React.useEffect(function(){
    var tableHost = document.getElementById('table');
    if (tableHost) {
      ReactDOM.render(
        h(StudentsTable, {
          students: props.students,
          menuOpenId: props.menuOpenId,
          onToggleMenu: props.onToggleMenu,
          onEdit: props.onEdit,
          onDelete: props.onDelete,
          t: props.t
        }),
        tableHost
      );
    }

    var pagerHost = document.getElementById('pager');
    if (pagerHost) {
      ReactDOM.render(
        h(window.Pagination, {
          page: props.page,
          totalPages: props.totalPages,
          pageSize: props.pageSize,
          totalElements: props.totalElements,
          onPrev: props.onPrev,
          onNext: props.onNext,
          onGoto: props.onGoto,
          onPageSize: props.onPageSize,
          t: props.t
        }),
        pagerHost
      );
    }
  }, [
    props.students,
    props.menuOpenId,
    props.onToggleMenu,
    props.onEdit,
    props.onDelete,
    props.page,
    props.totalPages,
    props.pageSize,
    props.totalElements,
    props.onPrev,
    props.onNext,
    props.onGoto,
    props.onPageSize,
    props.t
  ]);
  return null;
}

ReactDOM.render(h(App), document.getElementById('app'));
