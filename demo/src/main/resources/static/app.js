const h = React.createElement;

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
    h('div', { className: 'error-slot ' + (hasErr ? '' : 'hidden') },
      hasErr ? props.error : 'placeholder'
    )
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

/* App */

const App = () => {
  const [students, setStudents] = React.useState([]);
  const [name, setName] = React.useState('');
  const [email, setEmail] = React.useState('');
  const [dob, setDob] = React.useState(''); // yyyy-MM-dd
  const [editingId, setEditingId] = React.useState(null);
  const [menuOpenId, setMenuOpenId] = React.useState(null);

  const [errors, setErrors] = React.useState({ name: '', email: '', dob: '', global: '' });

  const loadStudents = React.useCallback(function(){
    fetch('/api/v1/registration/students')
      .then(function(r){ return r.json(); })
      .then(function(data){ setStudents(Array.isArray(data) ? data : []); })
      .catch(function(){});
  }, []);
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
    if (hasFieldErrors(next)) {
      next.global = ''; // do not show global if field errors exist
    } else {
      next.global = msg || 'Request failed';
    }
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
    await loadStudents();
    setName(''); setEmail(''); setDob('');
  }

  async function handleUpdate(){
    if (!editingId) return;
    setErrors({ name: '', email: '', dob: '', global: '' });
    var url = '/api/v1/registration/' + editingId + '?name=' +
              encodeURIComponent(name) + '&email=' + encodeURIComponent(email);
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

  // Banner node
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
        h('button', {
          className: 'btn',
          onClick: editingId ? handleUpdate : handleCreate
        }, editingId ? 'Update Student' : 'Add Student')
      )
    ),

    // List card is rendered into #table (keep structure minimal)
    h(TableMountHelper, {
      students: students,
      menuOpenId: menuOpenId,
      onToggleMenu: toggleMenu,
      onEdit: handleEditRow,
      onDelete: handleDelete
    })
  );
};

/* Mount helper to render table into the second card */
function TableMountHelper(props){
  React.useEffect(function(){
    var host = document.getElementById('table');
    if (!host) return;
    ReactDOM.render(
      h(StudentsTable, {
        students: props.students,
        menuOpenId: props.menuOpenId,
        onToggleMenu: props.onToggleMenu,
        onEdit: props.onEdit,
        onDelete: props.onDelete
      }),
      host
    );
  }, [props.students, props.menuOpenId, props.onToggleMenu, props.onEdit, props.onDelete]);
  return null;
}

ReactDOM.render(h(App), document.getElementById('app'));
