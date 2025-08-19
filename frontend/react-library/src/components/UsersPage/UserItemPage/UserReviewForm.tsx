import { useState } from "react";
import { useParams } from "react-router-dom";
import { SubmitButton } from "../../Utils/SubmitButton";
import { useAuthService } from "../../Utils/useAuthService";
import { useFormErrors } from "../../Utils/useFormErrors";

export const UserReviewForm: React.FC<{
    review: { text: string, rating: number }
    setReview: (review: { text: string, rating: number } | null) => void
}> = (props) => {
    const { userId } = useParams();

    const { handleLogOut } = useAuthService();

    const [text, setText] = useState<string>(props.review.text);
    const [rating, setRating] = useState<number>(props.review.rating);
    const { errors, setFieldError, handleBackendErrors, clearErrors } = useFormErrors();

    const makeNewReview = async (e: React.FormEvent) => {
        e.preventDefault();
        clearErrors();

        const token = localStorage.getItem("token");

        try {
            const response = await fetch(`http://localhost:8081/api/reviews/to/${userId}`, {
                method: props.review.text === "" && props.review.rating === 0 ? "POST" : "PUT",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`,
                },
                body: JSON.stringify({ text, rating })
            });

            if (response.status === 403 || response.status === 401) {
                handleLogOut();
                return Promise.reject(new Error('Authentication failed'));
            }
            if (!response.ok) {
                await handleBackendErrors(response);
                return;
            }

            props.setReview({ text, rating });
            alert("Updated");
        } catch (err: any) {
            setFieldError("general", err.message);
        }
    };

    const deleteReview = async () => {
        const token = localStorage.getItem("token");

        fetch(`http://localhost:8081/api/reviews/to/${userId}`, {
            method: "DELETE",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
            }
        });

        setText("");
        setRating(0);
        props.setReview(null);
        alert("Deleted");
    };

    return (
        <form onSubmit={makeNewReview}>
            <div className="d-flex">
                <div className="mb-3 w-100">
                    <label htmlFor="text" className="form-label">Review</label>
                    <textarea className={`form-control ${errors.text && "is-invalid"}`} id="text" rows={3} value={text}
                        onChange={(e) => setText(e.target.value)}></textarea>
                    {errors.text && <div className="invalid-feedbackd text-danger">{errors.text}</div>}
                </div>
                <div className="d-flex flex-column justify-content-center align-items-center">
                    <div className="d-flex align-items-center ms-3 gap-1">
                        {[...Array(5)].map((_, index) => {
                            const currentRating = index + 1;
                            const isFilled = rating >= currentRating;
                            return (
                                <svg key={index} xmlns="http://www.w3.org/2000/svg" width="35" height="35" fill="currentColor" className="bi bi-heart text-danger" viewBox="0 0 16 16" onClick={() => setRating(currentRating)} style={{ cursor: "pointer" }}>
                                    <path d={isFilled ? "M8 1.314C12.438-3.248 23.534 4.735 8 15-7.534 4.736 3.562-3.248 8 1.314" :
                                        "m8 2.748-.717-.737C5.6.281 2.514.878 1.4 3.053c-.523 1.023-.641 2.5.314 4.385.92 1.815 2.834 3.989 6.286 6.357 3.452-2.368 5.365-4.542 6.286-6.357.955-1.886.838-3.362.314-4.385C13.486.878 10.4.28 8.717 2.01zM8 15C-7.333 4.868 3.279-3.04 7.824 1.143q.09.083.176.171a3 3 0 0 1 .176-.17C12.72-3.042 23.333 4.867 8 15"} />
                                </svg>
                            );
                        })}
                    </div>
                    {errors.rating && <div className="invalid-feedbackd text-danger">{errors.rating}</div>}
                </div>

            </div>
            {errors.general && <div className="text-danger">{errors.general}</div>}
            <SubmitButton />

            {(props.review.text !== "" || props.review.rating !== 0) &&
                <button type="button" className="position-absolute top-0 end-0 btn btn-outline-danger" onClick={deleteReview}>
                    Delete Review
                </button>
            }
        </form>
    );
}