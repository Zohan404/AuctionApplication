import { Link } from "react-router-dom";

export const ErrorPage = () => {
    return (
        <main className="position-absolute top-50 start-50 translate-middle text-center">
            <svg xmlns="http://www.w3.org/2000/svg" width="200" height="200" fill="currentColor" className="bi bi-exclamation-triangle-fill text-danger" viewBox="0 0 16 16">
                <path d="M8.982 1.566a1.13 1.13 0 0 0-1.96 0L.165 13.233c-.457.778.091 1.767.98 1.767h13.713c.889 0 1.438-.99.98-1.767zM8 5c.535 0 .954.462.9.995l-.35 3.507a.552.552 0 0 1-1.1 0L7.1 5.995A.905.905 0 0 1 8 5m.002 6a1 1 0 1 1 0 2 1 1 0 0 1 0-2" />
            </svg>
            <h1 className="display-1 fw-bold text-bg-danger">NOT FOUND</h1>
            <div className="h4">This content is currently unavailable!</div>
            <div className="lead w-75 m-auto mb-3">The address you are searching for might not exist or the content has been deleted.</div>
            <Link className="btn btn-primary" to="/home">Back to Home</Link>
        </main>
    );
}