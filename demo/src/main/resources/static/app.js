// app.js (ES5-friendly)

const App = () => {
  const [students, setStudents] = React.useState([]);
  const [name, setName] = React.useState('');
  const [email, setEmail] = React.useState('');
  const [dob, setDob] = React.useState(''); // yyyy-MM-dd
  const [editingStudent, setEditingStudent] = React.useState(null);

  const [errors, setErrors] = React.useState({ name: '', email: '', dob: '', global: '' });

  const loadStudents = React.useCallback(() => {
    fetch('/api/v1/registration/students')
      .then(function(r){ return r.json(); })
      .then(function(data){ setStudents(data); })
      .catch(function(){});
  }, []);

  React.useEffect(function(){
    loadStudents();
  }, [loadStudents]);

  function mapApiErrorsToState(apiError) {
    var next = { name: '', email: '', dob: '', global: '' };

    if (apiError && Array.isArray(apiError.fields)) {
      apiError.fields.forEach(function(f){
        var field = ((f && f.field) ? f.field : '').toLowerCase();
        if (field && Object.prototype.hasOwnProperty.call(next, field)) {
          next[field] = (f && f.message) ? f.message : 'Invalid value';
        }
      });
    }

    var apiMessage = apiError && apiError.message ? apiError.message : null;

    // If no field error set, show generic/global message
    var hasAnyFieldError = !!(next.name || next.email || next.dob);
    if (!hasAnyFieldError) {
      next.global = apiMessage || 'Request failed';
    } else if (apiMessage) {
      next.global = apiMessage; // keep banner + field errors
    }
    return next;
  }

  function safeJson(res) {
    return res.json().catch(function(){ return null; });
  }

  const handleCreate = async () => {
    setErrors({ name: '', email: '', dob: '', global: '' });

    var payload = { name: name, email: email, dob: dob };
    const res = await fetch('/api/v1/registration', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    });

    if (!res.ok) {
      const body = await safeJson(res);
      setErrors(mapApiErrorsToState(body));
      return;
    }

    await loadStudents();
    setName('');
    setEmail('');
    setDob('');
  };

  const handleUpdate = async (id) => {
    setErrors({ name: '', email: '', dob: '', global: '' });

    const res = await fetch('/api/v1/registration/' + id + '?name=' +
      encodeURIComponent(name) + '&email=' + encodeURIComponent(email), { method: 'PUT' });

    if (!res.ok) {
      const body = await safeJson(res);
      setErrors(mapApiErrorsToState(body));
      return;
    }

    await loadStudents();
    setName('');
    setEmail('');
    setDob('');
    setEditingStudent(null);
  };

  const handleDelete = async (id) => {
    await fetch('/api/v1/registration/' + id, { method: 'DELETE' });
    await loadStudents();
  };

  const handleEdit = (student) => {
    setEditingStudent(student.id);
    setName(student && student.name ? student.name : '');
    setEmail(student && student.email ? student.email : '');
    setDob(((student && student.dob) ? student.dob : '').slice(0, 10));
    setErrors({ name: '', email: '', dob: '', global: '' });
  };

  const inputClass = (hasError) => 'input ' + (hasError ? 'input-error' : '');

  return (
    React.createElement('div', null,
      React.createElement('h1', null, 'Student Management'),

      errors.global ? React.createElement('div', { className: 'banner-error', role: 'alert' }, errors.global) : null,

      React.createElement('div', { className: 'form-row' },
        React.createElement('div', { className: 'field' },
          React.createElement('input', {
            className: inputClass(!!errors.name),
            type: 'text',
            placeholder: 'Name',
            value: name,
            onChange: function(e){ setName(e.target.value); if (errors.name) setErrors(Object.assign({}, errors, { name: '' })); }
          }),
          errors.name ? React.createElement('div', { className: 'field-error' }, errors.name) : null
        ),

        React.createElement('div', { className: 'field' },
          React.createElement('input', {
            className: inputClass(!!errors.email),
            type: 'email',
            placeholder: 'Email',
            value: email,
            onChange: function(e){ setEmail(e.target.value); if (errors.email) setErrors(Object.assign({}, errors, { email: '' })); }
          }),
          errors.email ? React.createElement('div', { className: 'field-error' }, errors.email) : null
        ),

        React.createElement('div', { className: 'field' },
          React.createElement('input', {
            className: inputClass(!!errors.dob),
            type: 'date',
            placeholder: 'Date of Birth',
            value: dob,
            onChange: function(e){ setDob(e.target.value); if (errors.dob) setErrors(Object.assign({}, errors, { dob: '' })); }
          }),
          errors.dob ? React.createElement('div', { className: 'field-error' }, errors.dob) : null
        ),

        editingStudent ?
          React.createElement('button', { onClick: function(){ handleUpdate(editingStudent); } }, 'Update Student') :
          React.createElement('button', { onClick: handleCreate }, 'Add Student')
      ),

      React.createElement('table', null,
        React.createElement('thead', null,
          React.createElement('tr', null,
            React.createElement('th', null, 'Name'),
            React.createElement('th', null, 'Email'),
            React.createElement('th', null, 'DOB'),
            React.createElement('th', null, 'Actions')
          )
        ),
        React.createElement('tbody', null,
          students.map(function(s){
            return React.createElement('tr', { key: s.id },
              React.createElement('td', null, s.name),
              React.createElement('td', null, s.email),
              React.createElement('td', null, ((s.dob || '')).slice(0, 10)),
              React.createElement('td', null,
                React.createElement('button', { onClick: function(){ handleEdit(s); } }, 'Edit'),
                ' ',
                React.createElement('button', { onClick: function(){ handleDelete(s.id); } }, 'Delete')
              )
            );
          })
        )
      )
    )
  );
};

ReactDOM.render(React.createElement(App, null), document.getElementById('root'));
