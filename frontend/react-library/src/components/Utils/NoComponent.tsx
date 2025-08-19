export const NoComponent: React.FC<{
    type: string;
    size: number;
}> = (props) => {
    return (
        <div className="d-flex flex-column justify-content-center align-items-center gap-3 p-5">
            <svg xmlns="http://www.w3.org/2000/svg" width={props.size} height={props.size} fill="currentColor"
                className="bi bi-x-circle-fill text-danger" viewBox="0 0 16 16">
                <path d="M16 8A8 8 0 1 1 0 8a8 8 0 0 1 16 0M5.354 4.646a.5.5 0 1 0-.708.708L7.293 8l-2.647 2.646a.5.5 0
                0 0.708.708L8 8.707l2.646 2.647a.5.5 0 0 0 .708-.708L8.707 8l2.647-2.646a.5.5 0 0 0-.708-.708L8 7.293z" />
            </svg>
            <div className="lead fw-medium text-danger">
                There are no {props.type} to show!
            </div>
        </div>
    );
}