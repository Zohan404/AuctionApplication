import { Navigate } from 'react-router-dom';

interface ProtectedRouteProps {
    children: React.ReactElement;
    location: string;
    needsAuth: boolean;
}

export const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ children, location, needsAuth }) => {
    const token = localStorage.getItem("token");

    if ((needsAuth && !token) || (!needsAuth && token)) {
        return <Navigate to={location} replace />;
    }

    return children;
};