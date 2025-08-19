export const ModalHeader: React.FC<{
    children: React.ReactNode
}> = (props) => {
    return (
        <div className="modal-header main-color bg-gradient border-bottom border-4 border-black">
            <div className="h1 text-capitalize fw-medium text-white m-0">{props.children}</div>
            <button type="button" className="btn-close bg-white position-absolute end-0 m-2" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
    );
}