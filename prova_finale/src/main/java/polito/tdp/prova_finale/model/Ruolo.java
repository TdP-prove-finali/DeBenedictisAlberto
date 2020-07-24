package polito.tdp.prova_finale.model;

public class Ruolo implements Comparable<Ruolo>{

	private Integer id;
	private String name;
	private String description;

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return name + " - " + description;
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

	
	// Due ruoli sono uguali se hanno lo stesso ID
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Ruolo other = (Ruolo) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public int compareTo(Ruolo o) {
		// TODO Auto-generated method stub
		return this.getId().compareTo(o.getId());
	}
	
	
	
	

}
