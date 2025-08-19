export const FollowersTabPlaceHolder: React.FC<{
    size: string
}> = (props) => {
    return (
        <div className="d-flex justify-content-center">
            <div className={`placeholder-glow d-flex flex-nowrap gap-1 ${props.size} px-2 fw-normal`}>
                <span className="placeholder bg-dark rounded" style={{ width: 70, height: 24 }}></span>
                <span className="placeholder bg-dark rounded-pill" style={{ width: 30, height: 24 }}></span>
            </div>
            <div className="vr"></div>
            <div className={`placeholder-glow d-flex flex-nowrap gap-1 ${props.size} px-2 fw-normal`}>
                <span className="placeholder bg-dark rounded-pill" style={{ width: 30, height: 24 }}></span>
                <span className="placeholder bg-dark rounded" style={{ width: 80, height: 24 }}></span>
            </div>
        </div>
    );
};