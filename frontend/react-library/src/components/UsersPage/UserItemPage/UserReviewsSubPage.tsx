import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { Review } from "../../Models/Review";
import { User } from "../../Models/User";
import { nameShower } from "../../Utils/helper";
import { NoComponent } from "../../Utils/NoComponent";
import { ProfilePicture } from "../../Utils/ProfilePicture";
import { Spinner } from "../../Utils/Spinner";
import { useAuthService } from "../../Utils/useAuthService";
import { UserReviewForm } from "./UserReviewForm";

export const UserReviewsSubPage: React.FC = () => {
    const { userId } = useParams();

    const { getLoggedInUser, handleLogOut } = useAuthService();
    const [loggedInUser] = useState<User | null>(getLoggedInUser());

    const [loading, setLoading] = useState<boolean>(true);
    const [reviews, setReviews] = useState<Review[]>([]);
    const [myReview, setMyReview] = useState<{ text: string, rating: number } | null>(null);
    const [page, setPage] = useState<number>(0);
    const [hasMore, setHasMore] = useState<boolean>(true);

    const fetchReviews = () => {
        const token = localStorage.getItem("token");

        setLoading(true);
        const params = new URLSearchParams();
        params.set("page", page.toString());

        fetch(`http://localhost:8081/api/reviews/user/${userId}?${params.toString()}`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
            },
        })
            .then((res) => {
                if (res.status === 403 || res.status === 401) {
                    handleLogOut();
                    return Promise.reject(new Error('Authentication failed'));
                }
                return res.json();
            })
            .then((json) => {
                setReviews(json);
                const match: Review = json.find((review: Review) => review.user.id === loggedInUser?.id);
                if (match) setMyReview({ text: match.text, rating: match.rating });
                if (json.length < 10) setHasMore(false);
            })
            .catch()
            .finally(() => setLoading(false));
    }

    useEffect(() => {
        fetchReviews();
    }, [userId, page]);

    useEffect(() => {
        const handleScroll = () => {
            if (window.innerHeight + window.scrollY >= document.body.offsetHeight && hasMore && !loading) {
                setPage(prev => prev + 1);
            }
        };

        window.addEventListener("scroll", handleScroll);
        return () => window.removeEventListener("scroll", handleScroll);
    }, [hasMore, loading]);

    return (
        (loading && page === 0)
            ?
            <div className="accordion placeholder-glow" id="reviewsAccordion">
                {[...Array(3)].map((_, index) => (
                    <div key={index} className="accordion-item">
                        <h2 className="accordion-header">
                            <button className="accordion-button" type="button">
                                <div className="d-flex align-items-center justify-content-between w-100">
                                    <div className="d-flex align-items-center w-100">
                                        <div className="rounded-circle bg-secondary placeholder" style={{ width: 50, height: 50 }}></div>
                                        <span className="placeholder h2 col-5 m-0 ms-3"></span>
                                    </div>
                                    <div className="d-flex gap-1 me-3">
                                        {[...Array(5)].map((_, index) => (
                                            <svg key={index} xmlns="http://www.w3.org/2000/svg" width="35" height="35" fill="currentColor" className="bi bi-heart-fill" viewBox="0 0 16 16">
                                                <path fillRule="evenodd" d="M8 1.314C12.438-3.248 23.534 4.735 8 15-7.534 4.736 3.562-3.248 8 1.314" />
                                            </svg>
                                        ))}
                                    </div>
                                </div>
                            </button>
                        </h2>
                    </div>
                ))}
            </div>
            :
            <>
                <div className="accordion" id="reviewsAccordion">
                    {loggedInUser?.id + "" !== userId &&
                        <div className="accordion-item rounded-top-0">
                            {myReview !== null
                                ?
                                <h2 className="accordion-header">
                                    <button className="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#collapseZero" aria-expanded="false" aria-controls="collapseZero">
                                        <div className="d-flex align-items-center w-100">
                                            <ProfilePicture pictureUrl={loggedInUser?.profilePicture} rounded={true} size={50} />
                                            <h2 className="display-6 fw-bold ms-2 text-danger text-decoration-underline">{nameShower(loggedInUser)}</h2>
                                        </div>
                                    </button>
                                </h2>
                                :
                                <h2 className="accordion-header">
                                    <button className="accordion-button collapsed btn btn-link hover link-opacity-10-hover w-100" type="button" data-bs-toggle="collapse" data-bs-target="#collapseZero" aria-expanded="false" aria-controls="collapseZero">
                                        <div className="d-flex justify-content-center align-items-center w-100">
                                            <svg xmlns="http://www.w3.org/2000/svg" width="50" height="50" fill="currentColor" className="bi bi-plus-square" viewBox="0 0 16 16">
                                                <path d="M14 1a1 1 0 0 1 1 1v12a1 1 0 0 1-1 1H2a1 1 0 0 1-1-1V2a1 1 0 0 1 1-1zM2 0a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V2a2 2 0 0 0-2-2z" />
                                                <path d="M8 4a.5.5 0 0 1 .5.5v3h3a.5.5 0 0 1 0 1h-3v3a.5.5 0 0 1-1 0v-3h-3a.5.5 0 0 1 0-1h3v-3A.5.5 0 0 1 8 4" />
                                            </svg>
                                        </div>
                                    </button>
                                </h2>
                            }
                            <div id="collapseZero" className="accordion-collapse collapse" data-bs-parent="#reviewsAccordion">
                                <div className="accordion-body position-relative">
                                    {myReview !== null
                                        ?
                                        <UserReviewForm review={myReview} setReview={(r) => setMyReview(r)} />
                                        :
                                        <UserReviewForm review={{ text: "", rating: 0 }}
                                            setReview={(r) => setMyReview(r)} />
                                    }
                                </div>
                            </div>
                        </div>
                    }
                    {reviews.length === 0
                        ?
                        <NoComponent type="reviews" size={200} />
                        :
                        reviews
                            .filter(r => r.user.id !== loggedInUser?.id)
                            .map((review, index) =>
                                <div key={index} className="accordion-item rounded-top-0">
                                    <h2 className="accordion-header">
                                        <button className="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target={`#collapse${index}`} aria-expanded="false" aria-controls={`collapse${index}`}>
                                            <div className="d-flex align-items-center w-100">
                                                <ProfilePicture pictureUrl={review.user.profilePicture} rounded={true} size={50} />
                                                <h2 className="display-6 fw-bold ms-2">{nameShower(review.user)}</h2>
                                            </div>
                                            <div className="d-flex gap-1 me-3">
                                                {[...Array(review.rating)].map((_, index) => (
                                                    <svg key={review.user.id + " " + index} xmlns="http://www.w3.org/2000/svg" width="35" height="35" fill="currentColor" className="bi bi-heart-fill text-danger" viewBox="0 0 16 16">
                                                        <path fillRule="evenodd" d="M8 1.314C12.438-3.248 23.534 4.735 8 15-7.534 4.736 3.562-3.248 8 1.314" />
                                                    </svg>
                                                ))}
                                                {[...Array(5 - review.rating)].map((_, index) => (
                                                    <svg key={index} xmlns="http://www.w3.org/2000/svg" width="35" height="35" fill="currentColor" className="bi bi-heart text-danger" viewBox="0 0 16 16">
                                                        <path d="m8 2.748-.717-.737C5.6.281 2.514.878 1.4 3.053c-.523 1.023-.641 2.5.314 4.385.92 1.815 2.834 3.989 6.286 6.357 3.452-2.368 5.365-4.542 6.286-6.357.955-1.886.838-3.362.314-4.385C13.486.878 10.4.28 8.717 2.01zM8 15C-7.333 4.868 3.279-3.04 7.824 1.143q.09.083.176.171a3 3 0 0 1 .176-.17C12.72-3.042 23.333 4.867 8 15" />
                                                    </svg>
                                                ))}
                                            </div>

                                        </button>
                                    </h2>
                                    <div id={`collapse${index}`} className="accordion-collapse collapse" data-bs-parent="#reviewsAccordion">
                                        <div className="accordion-body">
                                            <div className="h1">Review</div>
                                            <div className="lean">{review.text}</div>
                                        </div>
                                    </div>
                                </div>
                            )}
                </div>
                {
                    (loading && page !== 0) &&
                    <Spinner />
                }
            </>
    );
}