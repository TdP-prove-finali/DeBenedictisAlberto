package polito.tdp.prova_finale.model;

public class Player {

	private Integer id;
	private Integer overall;
	private String position;
	private Integer price;
	private String name;
	private String ext_name;
	private String quality;
	private String club;
	private String league;
	private String nationality;
	
	
	
	
	
	public Player(Integer id, Integer overall, String position, Integer price, String name, String ext_name,
			String quality, String club, String league, String nationality) {
		super();
		this.id = id;
		this.overall = overall;
		this.position = position;
		this.price = price;
		this.name = name;
		this.ext_name = ext_name;
		this.quality = quality;
		this.club = club;
		this.league = league;
		this.nationality = nationality;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getOverall() {
		return overall;
	}
	public void setOverall(Integer overall) {
		this.overall = overall;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public Integer getPrice() {
		return price;
	}
	public void setPrice(Integer price) {
		this.price = price;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getExt_name() {
		return ext_name;
	}
	public void setExt_name(String ext_name) {
		this.ext_name = ext_name;
	}
	public String getQuality() {
		return quality;
	}
	public void setQuality(String quality) {
		this.quality = quality;
	}
	public String getClub() {
		return club;
	}
	public void setClub(String club) {
		this.club = club;
	}
	public String getLeague() {
		return league;
	}
	public void setLeague(String league) {
		this.league = league;
	}
	public String getNationality() {
		return nationality;
	}
	public void setNationality(String nationality) {
		this.nationality = nationality;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return name+" "+overall+" "+position+" "+price;
	}
	
	
	
	
	
}
