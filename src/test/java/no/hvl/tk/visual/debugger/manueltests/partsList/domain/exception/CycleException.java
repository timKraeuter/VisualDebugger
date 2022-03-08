package no.hvl.tk.visual.debugger.manueltests.partsList.domain.exception;

/**
 * Indicates that a cycle would occur, if the component is added to the product.
 */
public class CycleException extends RuntimeException {

    public static final String CYCLES_ARE_NOT_ALLOWED_IN_A_PARTS_LIST = "Cycles are not allowed in a parts list!";

    public CycleException() {
        super(CYCLES_ARE_NOT_ALLOWED_IN_A_PARTS_LIST);
    }
}
