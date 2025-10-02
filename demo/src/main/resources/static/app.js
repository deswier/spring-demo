const App = () => {
    const [students, setStudents] = React.useState([]);
    const [name, setName] = React.useState('');
    const [email, setEmail] = React.useState('');
    const [dob, setDob] = React.useState('');
    const [editingStudent, setEditingStudent] = React.useState(null);

    React.useEffect(() => {
        fetch('/api/v1/registration/students')
            .then(response => response.json())
            .then(data => setStudents(data));
    }, []);

    const handleCreate = () => {
        fetch('/api/v1/registration', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ name, email, dob }),
        })
            .then(() => {
                fetch('/api/v1/registration/students')
                    .then(response => response.json())
                    .then(data => setStudents(data));
                setName('');
                setEmail('');
                setDob('');
            });
    };

    const handleUpdate = (id) => {
        fetch(`/api/v1/registration/${id}?name=${name}&email=${email}`, {
            method: 'PUT',
        })
            .then(() => {
                fetch('/api/v1/registration/students')
                    .then(response => response.json())
                    .then(data => setStudents(data));
                setName('');
                setEmail('');
                setDob('');
                setEditingStudent(null);
            });
    };

    const handleDelete = (id) => {
        fetch(`/api/v1/registration/${id}`, {
            method: 'DELETE',
        })
            .then(() => {
                fetch('/api/v1/registration/students')
                    .then(response => response.json())
                    .then(data => setStudents(data));
            });
    };

    const handleEdit = (student) => {
        setEditingStudent(student.id);
        setName(student.name);
        setEmail(student.email);
        setDob(student.dob);
    };

    return (
        <div>
            <h1>Student Management</h1>
            <div>
                <input type="text" placeholder="Name" value={name} onChange={(e) => setName(e.target.value)} />
                <input type="email" placeholder="Email" value={email} onChange={(e) => setEmail(e.target.value)} />
                <input type="date" placeholder="Date of Birth" value={dob} onChange={(e) => setDob(e.target.value)} />
                {editingStudent ? (
                    <button onClick={() => handleUpdate(editingStudent)}>Update Student</button>
                ) : (
                    <button onClick={handleCreate}>Add Student</button>
                )}
            </div>
            <table>
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Email</th>
                    <th>DOB</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                {students.map(student => (
                    <tr key={student.id}>
                        <td>{student.name}</td>
                        <td>{student.email}</td>
                        <td>{student.dob}</td>
                        <td>
                            <button onClick={() => handleEdit(student)}>Edit</button>
                            <button onClick={() => handleDelete(student.id)}>Delete</button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
};

ReactDOM.render(<App />, document.getElementById('root'));
