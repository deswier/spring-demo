const h = React.createElement;

/* ---------- Reusable InputField component ---------- */
function InputField(props) {
    const hasErr = !!props.error;
    return h('div', { className: 'field' },
        h('label', { className: 'label' }, props.label),
        h('input', {
            className: 'input ' + (hasErr ? 'input-error' : ''),
            type: props.type || 'text',
            placeholder: props.placeholder || '',
            value: props.value,
            onChange: (ev) => props.onChange(ev.target.value)
        }),
        h('div', { className: 'error-slot ' + (hasErr ? '' : 'hidden') }, hasErr ? props.error : 'placeholder')
    );
}

/* ---------- Login Form Component ---------- */
const LoginForm = ({ onSwitchToRegister, t }) => {
    const [email, setEmail] = React.useState('');
    const [password, setPassword] = React.useState('');
    const [error, setError] = React.useState('');

    const handleLogin = async (event) => {
        event.preventDefault();
        setError('');

        const body = new URLSearchParams();
        body.append('email', email); // Spring Security's default is 'username'
        body.append('password', password);

        const res = await fetch('/api/v1/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: body,
        });

        if (res.ok) {
            window.location.href = '/';
        } else {
            setError(t('auth.error.invalid.credentials'));
        }
    };

    return h('div', null,
        h('div', { className: 'card-header' }, h('h1', null, t('auth.signin.title'))),
        h('div', { className: 'card-body' },
            error && h('div', { className: 'alert' }, error),
            h('form', { onSubmit: handleLogin },
                h(InputField, { label: t('auth.form.email.label'), value: email, onChange: setEmail, type: 'email' }),
                h(InputField, { label: t('auth.form.password.label'), value: password, onChange: setPassword, type: 'password' }),
                h('div', { className: 'field' },
                    h('button', { type: 'submit', className: 'btn' }, t('auth.signin.button'))
                )
            ),
            h('div', { style: { marginTop: '1rem', textAlign: 'center' } },
                h('a', { href: '#', onClick: onSwitchToRegister }, t('auth.link.signup'))
            )
        )
    );
};

/* ---------- Registration Form Component ---------- */
const RegistrationForm = ({ onSwitchToLogin, t }) => {
    const [firstName, setFirstName] = React.useState('');
    const [lastName, setLastName] = React.useState('');
    const [email, setEmail] = React.useState('');
    const [password, setPassword] = React.useState('');

    const [error, setError] = React.useState('');
    const [successMessage, setSuccessMessage] = React.useState('');

    const handleRegister = async (event) => {
        event.preventDefault();
        setError('');
        setSuccessMessage('');

        if (!firstName || !lastName || !email || !password) {
            setError(t('auth.error.all.fields.required'));
            return;
        }

        const payload = { firstName, lastName, email, password };

        const res = await fetch('/api/v1/registration/user', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload),
        });

        if (!res.ok) {
            const body = await res.json().catch(() => ({ message: 'Registration failed. Please try again.' }));
            setError(body.message || 'An unknown error occurred.');
            return;
        }

        setFirstName('');
        setLastName('');
        setEmail('');
        setPassword('');
        setSuccessMessage(t('auth.success.registration'));

        // Automatically switch to the login view after a short delay
        setTimeout(() => {
            onSwitchToLogin({ preventDefault: () => {} }); // Pass a mock event to switch views
        }, 3000); // 3-second delay
    };

    return h('div', null,
        h('div', { className: 'card-header' }, h('h1', null, t('auth.signup.title'))),
        h('div', { className: 'card-body' },
            error && h('div', { className: 'alert' }, error),
            successMessage && h('div', { className: 'alert alert-success' }, successMessage),

            h('form', { onSubmit: handleRegister },
                h(InputField, {
                    label: t('auth.form.firstname.label'),
                    value: firstName,
                    onChange: setFirstName,
                }),
                h(InputField, {
                    label: t('auth.form.lastname.label'),
                    value: lastName,
                    onChange: setLastName,
                }),
                h(InputField, {
                    label: t('auth.form.email.label'),
                    value: email,
                    onChange: setEmail,
                    type: 'email',
                }),
                h(InputField, {
                    label: t('auth.form.password.label'),
                    value: password,
                    onChange: setPassword,
                    type: 'password',
                }),
                h('div', { className: 'field' },
                    h('button', { type: 'submit', className: 'btn' }, t('auth.signup.button'))
                )
            ),
            h('div', { style: { marginTop: '1rem', textAlign: 'center' } },
                h('a', { href: '#', onClick: onSwitchToLogin }, t('auth.link.signin'))
            )
        )
    );
};

/* ---------- Main Authentication App Component ---------- */
const AuthApp = () => {
    // Use URL hash to determine which view to show
    const [view, setView] = React.useState(window.location.hash === '#register' ? 'register' : 'login');

    const [messages, setMessages] = React.useState({});
    const lang = new URLSearchParams(window.location.search).get('lang') || 'ru';

    React.useEffect(function(){
        fetch('/api/v1/messages?lang=' + lang)
            .then(function(r){ return r.json(); })
            .then(setMessages)
            .catch(function(e){ console.error("Could not load messages", e); });
    }, [lang]);

    const t = React.useCallback(function(key){
        return messages[key] || key;
    }, [messages]);

    const handleHashChange = () => {
        setView(window.location.hash === '#register' ? 'register' : 'login');
    };

    React.useEffect(() => {
        window.addEventListener('hashchange', handleHashChange);
        return () => window.removeEventListener('hashchange', handleHashChange);
    }, []);

    const switchToRegister = (e) => {
        e.preventDefault();
        window.location.hash = 'register';
    };

    const switchToLogin = (e) => {
        e.preventDefault();
        window.location.hash = 'login';
    };

    let content;
    if (view === 'register') {
        content = h(RegistrationForm, { onSwitchToLogin: switchToLogin, t: t });
    } else {
        content = h(LoginForm, { onSwitchToRegister: switchToRegister, t: t });
    }

    return h('div', { className: 'card' }, Object.keys(messages).length > 0 ? content : null);
};

ReactDOM.render(h(AuthApp), document.getElementById('auth-app'));
