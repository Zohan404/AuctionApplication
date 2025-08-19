export const BidListPlaceHolder: React.FC<{
    displaySize: number,
    imageSize: number,
    isRounded: boolean
}> = (props) => {
    return (
        <li className="list-group-item d-flex align-items-center justify-content-between">
            <div className="d-flex w-100 align-items-center">
                <div className={`placeholder ${props.isRounded && "rounded-circle"}`}
                    style={{ width: props.imageSize, height: props.imageSize }}></div>
                <span className={`placeholder display-${props.displaySize} ms-3 col-6`}></span>
            </div>
            <div className="placeholder bg-primary rounded-pill" style={{ width: 70, height: 40 }}></div>
        </li>
    );
}