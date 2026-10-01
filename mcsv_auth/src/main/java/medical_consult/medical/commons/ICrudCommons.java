package medical_consult.medical.commons;


public interface ICrudCommons<Req, Res, ID> {

    public Res save(Req request);

    public Res update(ID id, Req request);

    public Res findById(ID id);

    public Res delete(ID id);

    
}