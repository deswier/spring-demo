// app.js — React 17 compatible, no JSX/optional chaining

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
    h('button', { className: 'menu-item', onClick: props.onEdit }, 'Edit'),
    h('button', { className: 'menu-item menu-danger', onClick: props.onDelete }, 'Delete')
  );
}

function StudentsTable(props){
  return h('table', null,
    h('thead', null,
      h('tr', null,
        h('th', null, 'Name'),
        h('th', null, 'Email'),
        h('th', null, 'Date of Birth'),
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
              onDelete: function(){ props.onDelete(s.id); }
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

  const loadStudents = React.useCallback(function(){
    fetch('/api/v1/registration/students?page=' + page + '&pageSize=' + pageSize)
      .then(function(r){ return r.json(); })
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
          next[field] = (f && f.message) ? f.message : 'Invalid value';
        }
      });
    }
    var msg = apiError && apiError.message ? apiError.message : null;
    next.global = hasFieldErrors(next) ? '' : (msg || 'Request failed');
    return next;
  }

  function safeJson(res){ return res.json().catch(function(){ return null; }); }

  async function handleCreate(){
    setErrors({ name: '', email: '', dob: '', global: '' });
    var payload = { name: name, email: email, dob: dob };
    var res = await fetch('/api/v1/registration', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    });
    if (!res.ok){
      var body = await safeJson(res);
      setErrors(mapApiErrorsToState(body));
      return;
    }
    // After adding, go to first page to show newest if your backend returns sorted pages differently; otherwise keep current
    // setPage(0);
    await loadStudents();
    setName(''); setEmail(''); setDob('');
  }

  async function handleUpdate(){
    if (!editingId) return;
    setErrors({ name: '', email: '', dob: '', global: '' });
    var url = '/api/v1/registration/' + editingId +
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
    await fetch('/api/v1/registration/' + id, { method: 'DELETE' });
    // If we removed the last item on the page, move one page back
    // Reload first, then adjust page if needed
    await loadStudents();
    if (menuOpenId === id) setMenuOpenId(null);
    // Optional: if current page becomes empty and not the first, go back
    // (Requires another fetch to know it's empty; skip for now to keep changes minimal)
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
    // Form
    h('div', { className: 'form-grid' },
      h(InputField, {
        label: 'Name',
        value: name,
        onChange: function(v){ setName(v); setErrors(function(p){ return Object.assign({}, p, { name: '' }); }); },
        error: errors.name
      }),
      h(InputField, {
        label: 'Email',
        value: email,
        onChange: function(v){ setEmail(v); setErrors(function(p){ return Object.assign({}, p, { email: '' }); }); },
        type: 'email',
        error: errors.email
      }),
      h(InputField, {
        label: 'Date of Birth',
        value: dob,
        onChange: function(v){ setDob(v); setErrors(function(p){ return Object.assign({}, p, { dob: '' }); }); },
        type: 'date',
        error: errors.dob
      }),
      h('div', null,
        h('button', { className: 'btn', onClick: editingId ? handleUpdate : handleCreate },
          editingId ? 'Update Student' : 'Add Student'
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
          onDelete: props.onDelete
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
          onPageSize: props.onPageSize
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
    props.onPageSize
  ]);
  return null;
}

ReactDOM.render(h(App), document.getElementById('app'));
