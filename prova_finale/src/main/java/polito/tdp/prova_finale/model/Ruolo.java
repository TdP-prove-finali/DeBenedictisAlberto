package polito.tdp.prova_finale.model;

public class Ruolo {

	private Integer id;
	private String name;
	private String description;

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return id + " " + name + " - " + description;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Ruolo(Integer id, String name, String description) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
	}

}
