import { useState } from "react";

export const useFormErrors = () => {
    const [errors, setErrors] = useState<Record<string, string>>({});

    const setFieldError = (field: string, message: string) => {
        setErrors(prev => ({ ...prev, [field]: message }));
    };

    const clearErrors = () => {
        setErrors({});
    };

    const handleBackendErrors = async (err: Response) => {
        try {
            const errorData = await err.json();
            if (typeof errorData === "object") {
                setErrors(errorData);
            } else {
                setErrors({ general: "Unknown error" });
            }
        } catch {
            setErrors({ general: "Unknown error" });
        }
    };

    return { errors, setFieldError, handleBackendErrors, clearErrors };
}