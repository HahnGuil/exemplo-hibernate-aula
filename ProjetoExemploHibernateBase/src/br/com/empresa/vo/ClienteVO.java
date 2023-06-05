package br.com.empresa.vo;

import java.io.Serializable;
import java.math.BigInteger;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "SICLIENT")
public class ClienteVO implements Serializable {

	private static final long serialVersionUID = 4342674836546855806L;

	@Id
	@Basic(optional = false)
	@NotNull
	@Column(name = "ID", nullable = false)
	@SequenceGenerator(name = "SQ_SICLIENT", sequenceName = "SQ_SICLIENT",
			allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.AUTO, 
		generator = "SQ_SICLIENT")
	private BigInteger id;

	//Nome do cliente - 100 caracteres
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 100)
	@Column(name = "descri", nullable = false, length = 100)
	private String descri;

	public ClienteVO() {
		super();
	}

	public ClienteVO(BigInteger id, String descri) {
		super();
		this.id = id;
		this.descri = descri;
	}

	public String getDescri() {
		return descri;
	}

	public void setDescri(String descri) {
		this.descri = descri;
	}

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
		ClienteVO other = (ClienteVO) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return descri;
	}

	public BigInteger getId() {
		return id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}

}
