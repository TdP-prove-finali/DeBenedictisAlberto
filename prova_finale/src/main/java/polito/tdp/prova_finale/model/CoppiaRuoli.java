package polito.tdp.prova_finale.model;

public class CoppiaRuoli {

	private Integer id1;
	private Integer id2;

	public CoppiaRuoli(Integer id1, Integer id2) {
		super();
		this.id1 = id1;
		this.id2 = id2;
	}

	public Integer getId1() {
		return id1;
	}

	public void setId1(Integer id1) {
		this.id1 = id1;
	}

	public Integer getId2() {
		return id2;
	}

	public void setId2(Integer id2) {
		this.id2 = id2;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return id1 + " - " + id2;
	}

}
