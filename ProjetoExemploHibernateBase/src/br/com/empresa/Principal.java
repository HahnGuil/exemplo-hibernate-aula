package br.com.empresa;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.criteria.Subquery;

import org.hibernate.Hibernate;

import br.com.empresa.dao.HibernateUtil;
import br.com.empresa.vo.ClienteVO;
import br.com.empresa.vo.ProdutoVO;

public class Principal {
	
	public static final String ASCENDING = "ASCENDING";
	public static final String DESCENDING = "DESCENDING";

	public Principal() {
	}

	public static void main(String[] args) {

		Principal p = new Principal();
		
		//p.consultarProdutoPorId();
		//p.consultarProdutoSimples();
		//p.consultarProdutoSimplesComTupla();
		//p.consultarProdutoTuplaJoin();
		//p.consultarProdutoTuplaJoinPaginacao(1, 3);
		
		/*
		ClienteVO cliente = new ClienteVO();
		cliente.setId(BigInteger.ONE);
		Map<String, Object> filters = new HashMap<String, Object>();
		filters.put("descri", "BOLACHA");
		filters.put("status", "I");
		
		p.consultarProdutoComTuplaJoinPaginacaoFiltroCompleto(
			0, 5, "id", Principal.ASCENDING, filters, cliente);
		*/
		
		/*
		Long qtd = p.consultarProdutoSimplesCount();
		System.out.println("Qtd >>> " + qtd);
		*/
		
		/*ClienteVO cliente = new ClienteVO();
		cliente.setId(BigInteger.ONE);
		Map<String, Object> filters = new HashMap<String, Object>();
		filters.put("descri", "BOLACHA");
		filters.put("status", "I");
		
		Long qtd = p.consultarProdutoSimplesCountFiltroCompleto(filters, cliente);
		System.out.println("Qtd >>>>>> " + qtd);
		*/
		
		//Consultas especiais.
		//p.consultarProdutoComMaiorIgual();
		p.consultarProdutoComSubQuery();
		
		
		p.consultarProduto();
		p.inserirProduto();
		p.editarProduto();
		p.excluirProduto();
		
		System.exit(0);

	}

	private void consultarProdutoComSubQuery() {
		
		System.out.println("Consulta com subquery");
		
		EntityManager em = HibernateUtil.getEntityManager();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		
		CriteriaQuery<ProdutoVO> criteria = cb.createQuery(ProdutoVO.class);
		
		//Cláusula FROM
		Root<ProdutoVO> produtoFrom = criteria.from(ProdutoVO.class);
		
		//Cláusula Where
		Predicate produtoWhere = 
				cb.like(cb.lower(produtoFrom.<String>get("descri")), "%agua%");
		
		//Subquery
		Subquery<Integer> subQuery = criteria.subquery(Integer.class);
		Root<ClienteVO> clienteFrom = subQuery.from(ClienteVO.class);
		subQuery.select(cb.literal(1));
		
		Predicate subQueryWhere = cb.like(clienteFrom.get("descri"), "%A%");
		subQueryWhere = cb.and(subQueryWhere, 
				cb.equal(clienteFrom, produtoFrom.get("client")));
		subQuery.where(subQueryWhere);
		
		produtoWhere = cb.and(produtoWhere, cb.exists(subQuery));
		
		Order produtoOrderBy = cb.asc(produtoFrom.get("descri"));
		
		criteria.select(produtoFrom);
		criteria.where(produtoWhere);
		criteria.orderBy(produtoOrderBy);
		
		TypedQuery<ProdutoVO> query = em.createQuery(criteria);
		
		List<ProdutoVO> ret = query.getResultList();
		
		for (ProdutoVO produtoVO : ret) {
			System.out.println("Produto >>> " + produtoVO.getId() + " - " +
					produtoVO.getDescri() + " - " + produtoVO.getQtdest());
		}
		
		em.close();
		
		System.out.println("Término da consulta com subquery");
		
	}

	private void consultarProdutoComMaiorIgual() {
		
		System.out.println("----------------consultar produto com maior igual----------------");
		
		EntityManager em = HibernateUtil.getEntityManager();
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ProdutoVO> criteria = cb.createQuery(ProdutoVO.class);
		
		//Cláusula From
		Root<ProdutoVO> produtoFrom = criteria.from(ProdutoVO.class);
		
		//Cláusula Where
		ClienteVO c1 = new ClienteVO();
		c1.setId(new BigInteger("1"));
		Predicate produtoWhere = cb.equal(produtoFrom.get("client"), c1);
		
		produtoWhere = cb.and(produtoWhere, 
				cb.greaterThanOrEqualTo(produtoFrom.get("id"), 10));
		
		produtoWhere = cb.and(produtoWhere, 
				cb.lessThanOrEqualTo(produtoFrom.get("id"), 15));
		
		
		//Cláusula orderBy
		Order produtoOrderBy = cb.asc(produtoFrom.get("descri"));
		
		//Atribuindo as cláusulas à consulta
		criteria.select(produtoFrom);
		criteria.where(produtoWhere);
		criteria.orderBy(produtoOrderBy);
		
		TypedQuery<ProdutoVO> query = em.createQuery(criteria);
				
		List<ProdutoVO> listaProdutos = query.getResultList();
		
		for (ProdutoVO produtoVO : listaProdutos) {
			System.out.println("Produto: " + produtoVO.getId() + " - " 
					+ produtoVO.getDescri() + " - " + produtoVO.getClient().getDescri());
		}
		
		em.close();
		
		System.out.println("----------------terminou----------------");
		
		
	}

	private Long consultarProdutoSimplesCountFiltroCompleto
		(Map<String, Object> filters, ClienteVO cliente) {
		
		System.out.println("Consultando quantidade de produtos com Filtro");
		
		EntityManager em = HibernateUtil.getEntityManager();
		CriteriaBuilder cb = em.getCriteriaBuilder();
				
		CriteriaQuery<Long> criteria = cb.createQuery(Long.class);
		
		//From
		Root<ProdutoVO> produtoFrom = criteria.from(ProdutoVO.class);
		
		//Select
		Selection<Long> produtoSelect = cb.count(produtoFrom);
		
		Predicate produtoWhere = cb.equal(produtoFrom.get("client"), cliente);
		
		if(filters != null) {
			
			for(Iterator<String> it = filters.keySet().iterator(); it.hasNext();) {
				
				String filterProperty = it.next();
				String filterValue = filters.get(filterProperty).toString();
				
				if(filterProperty.equals("id")) {
					
					produtoWhere = cb.and(produtoWhere, 
							cb.equal(produtoFrom.get(filterProperty), filterValue));
					
				}else {
					produtoWhere = cb.and(produtoWhere, 
							cb.like(cb.lower(produtoFrom.get(filterProperty)), 
									"%" + filterValue.toLowerCase() + "%"));
				}				
			}			
		}
		
		criteria.select(produtoSelect);
		criteria.where(produtoWhere);
		
		
		TypedQuery<Long> query = em.createQuery(criteria);
		
		Long ret = (Long) query.getSingleResult();
		
		em.close();
		
		System.out.println("Terminando consulta de quantidade de produtos");
		
		return ret;
	}

	private Long consultarProdutoSimplesCount() {
		
		System.out.println("Consultando quantidade de produtos");
		
		EntityManager em = HibernateUtil.getEntityManager();
		CriteriaBuilder cb = em.getCriteriaBuilder();
				
		CriteriaQuery<Long> criteria = cb.createQuery(Long.class);
		
		//From
		Root<ProdutoVO> produtoFrom = criteria.from(ProdutoVO.class);
		
		//Select
		Selection<Long> produtoSelect = cb.count(produtoFrom);
		
		criteria.select(produtoSelect);
		//criteria.where(produtoWhere);
		
		TypedQuery<Long> query = em.createQuery(criteria);
		
		Long ret = (Long) query.getSingleResult();
		
		em.close();
		
		System.out.println("Terminando consulta de quantidade de produtos");
		
		return ret;
	}

	private void consultarProdutoComTuplaJoinPaginacaoFiltroCompleto(
			Integer first, Integer pageSize, String sortField, String sortOrder,
			Map<String, Object> filters, ClienteVO cliente) {
		
		System.out.println("Começando filtro completo");
		
		EntityManager em = HibernateUtil.getEntityManager();
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		
		CriteriaQuery<Tuple> criteria = cb.createQuery(Tuple.class);
		
		//Cláusula From
		Root<ProdutoVO> produtoFrom = criteria.from(ProdutoVO.class);
		Join<ProdutoVO, ClienteVO> clienteFrom = produtoFrom.join("client");
		
		Path<BigInteger> produtoFrom_Id = produtoFrom.get("id");
		Path<String> produtoFrom_Descri = produtoFrom.get("descri");
		Path<BigDecimal> produtoFrom_Qtdest = produtoFrom.get("qtdest");
		
		criteria.multiselect(produtoFrom_Id, produtoFrom_Descri, produtoFrom_Qtdest, 
				clienteFrom);
		
		Predicate produtoWhere = cb.equal(clienteFrom, cliente);
		
		if(filters != null) {
			
			for(Iterator<String> it = filters.keySet().iterator(); it.hasNext();) {
				
				String filterProperty = it.next();
				String filterValue = filters.get(filterProperty).toString();
				
				if(filterProperty.equals("id")) {
					
					produtoWhere = cb.and(produtoWhere, 
							cb.equal(produtoFrom.get(filterProperty), filterValue));
					
				}else if(filterProperty.equals("client")) {
					
					produtoWhere = cb.and(produtoWhere, 
					cb.like(cb.lower(clienteFrom.get("descri")), 
							"%" + filterValue.toLowerCase() + "%"));
					
				}else {
					produtoWhere = cb.and(produtoWhere, 
							cb.like(cb.lower(produtoFrom.get(filterProperty)), 
									"%" + filterValue.toLowerCase() + "%"));
				}				
			}			
		}
		
		Order produtoOrderBy = cb.asc(produtoFrom.get("descri"));
		
		if(sortField != null) {
			
			if(sortOrder.equals(Principal.ASCENDING)) {
				produtoOrderBy = cb.asc(produtoFrom.get(sortField));
			}else if(sortOrder.equals(Principal.DESCENDING)) {
				produtoOrderBy = cb.desc(produtoFrom.get(sortField));
			}
		}
		
		//Where
		criteria.where(produtoWhere);
		//Order by
		criteria.orderBy(produtoOrderBy);
		
		TypedQuery<Tuple> query = em.createQuery(criteria);
		
		//Seta a quantidade máxima de elementos a serem retornados e primeiro.
		query.setFirstResult(first);
		if(pageSize != 0) {
			query.setMaxResults(pageSize);
		}
		
		List<Tuple> tuples = query.getResultList();
		
		//Monta o retorno da função.
		List<ProdutoVO> ret = new ArrayList<ProdutoVO>();
		if(tuples != null) {
			
			for(Tuple tuple : tuples) {
				
				ClienteVO clienteAux = tuple.get(clienteFrom);
				
				ProdutoVO produtoVO = new ProdutoVO();
				produtoVO.setId(tuple.get(produtoFrom_Id));
				produtoVO.setDescri(tuple.get(produtoFrom_Descri));
				produtoVO.setQtdest(tuple.get(produtoFrom_Qtdest));
				produtoVO.setClient(clienteAux);
				
				ret.add(produtoVO);
				
			}			
		}
		
		for(ProdutoVO produtoVO : ret) {
			System.out.println("Produto >>>>> " + produtoVO.getId() + " - " + 
					produtoVO.getDescri());
		}
		
		em.close();
		
		System.out.println("Terminando filtro completo");
	}

	private void consultarProdutoTuplaJoinPaginacao(Integer first, Integer pageSize) {
		
		System.out.println("Começando paginação");
		
		EntityManager em = HibernateUtil.getEntityManager();
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		
		CriteriaQuery<Tuple> criteria = cb.createQuery(Tuple.class);
		
		//Cláusula From com LEFT ALTER JOIN
		Root<ProdutoVO> produtoFrom = criteria.from(ProdutoVO.class);
		Join<ProdutoVO, ClienteVO> clienteFrom = produtoFrom.join("client", JoinType.LEFT);
		
		Path<BigInteger> produtoFrom_Id = produtoFrom.get("id");
		Path<String> produtoFrom_Descri = produtoFrom.get("descri");
		Path<BigDecimal> produtoFrom_Qtdest = produtoFrom.get("qtdest");
		
		criteria.multiselect(produtoFrom_Id, produtoFrom_Descri, produtoFrom_Qtdest,
				clienteFrom);
		
		//Cláusula Where
		Predicate produtoWhere = cb.equal( clienteFrom, BigInteger.ONE );
		
		produtoWhere = cb.and(produtoWhere, 
				cb.like(cb.lower(produtoFrom_Descri), "%agua%"));
		
		//Predicate aguaWhere = cb.like(cb.lower(produtoFrom_Descri), "%agua%");
		
		//Cláusula OrderBy
		Order produtoOrderDescri = cb.asc(produtoFrom.get("descri"));
		Order produtoOrderQtdest = cb.desc(produtoFrom.get("qtdest"));
		
		//criteria.where(produtoWhere, aguaWhere);
		criteria.where(produtoWhere);
		criteria.orderBy(produtoOrderDescri, produtoOrderQtdest);
		
		TypedQuery<Tuple> query = em.createQuery(criteria);
		
		//Paginação
		if(first != null) {
			query.setFirstResult(first);
		}
		
		if(pageSize != null) {
			query.setMaxResults(pageSize);
		}
		//TODO: PRESTAR ATENÇÃO PARA NÃO DIGITAR ERRADO!!!!!!
		List<ProdutoVO> ret = new ArrayList<ProdutoVO>();
		List<Tuple> tuples = query.getResultList();
		
		if(tuples != null) {
			for(Tuple tuple : tuples) {
				ClienteVO cliente = tuple.get(clienteFrom);
				
				ProdutoVO produto = new ProdutoVO();
				produto.setId(tuple.get(produtoFrom_Id));
				produto.setDescri(tuple.get(produtoFrom_Descri));
				produto.setQtdest(tuple.get(produtoFrom_Qtdest));
				produto.setClient(cliente);
				
				ret.add(produto);
			}
		}
		
		for(ProdutoVO produto : ret) {
			System.out.println("Produto >>> " + produto.getId() + " - " + 
					produto.getDescri());
		}
		
		em.close();
		
		System.out.println("Terminando paginação");
		
	}

	private void consultarProdutoTuplaJoin() {
		
		System.out.println("Consultando com tupla e utilizando Joins");
		
		EntityManager em = HibernateUtil.getEntityManager();
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> criteria = cb.createQuery(Tuple.class);
		
		//Cláusula From
		Root<ProdutoVO> produtoFrom = criteria.from(ProdutoVO.class);
		Join<ProdutoVO, ClienteVO> clienteFrom = produtoFrom.join("client");
		
		Path<BigInteger> produtoFrom_Id = produtoFrom.get("id");
		Path<String> produtoFrom_Descri = produtoFrom.get("descri");
		Path<BigDecimal> produtoFrom_Qtdest = produtoFrom.get("qtdest");
		
		//Cláusula Select
		criteria.multiselect(produtoFrom_Id, produtoFrom_Descri, produtoFrom_Qtdest);
		
		//Cláusula Where
		//Predicate produtoWhere = cb.equal(produtoFrom.get("client"), BigInteger.ONE);
		Predicate produtoWhere = cb.equal(clienteFrom, BigInteger.ONE);
		
		//Cláusula OrderBy
		Order produtoOrderBy = cb.asc(produtoFrom.get("descri"));
		
		criteria.where(produtoWhere);
		criteria.orderBy(produtoOrderBy);
				
		TypedQuery<Tuple> query = em.createQuery(criteria);
		query.setMaxResults(10);
		
		List<Tuple> tuples = query.getResultList();
		
		List<ProdutoVO> ret = new ArrayList<ProdutoVO>();
		if(tuples != null) {
			for(Tuple tuple : tuples) {
				ProdutoVO produtoVO = new ProdutoVO(tuple.get(produtoFrom_Id));
				produtoVO.setDescri(tuple.get(produtoFrom_Descri));
				produtoVO.setQtdest(tuple.get(produtoFrom_Qtdest));
				ret.add(produtoVO);
			}
		}
		
		for (ProdutoVO produtoVO : ret) {
			System.out.println("Produto: " + produtoVO.getId() + " - " 
					+ produtoVO.getDescri());
		}
		
		//Fecha o entity manager
		em.close();
		
		System.out.println("Término da consulta com tupla");
		
		
	}

	private void consultarProdutoSimplesComTupla() {
		
		System.out.println("Consultando com tupla");
		
		EntityManager em = HibernateUtil.getEntityManager();
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> criteria = cb.createQuery(Tuple.class);
		
		//Cláusula From
		Root<ProdutoVO> produtoFrom = criteria.from(ProdutoVO.class);
		
		Path<BigInteger> produtoFrom_Id = produtoFrom.get("id");
		Path<String> produtoFrom_Descri = produtoFrom.get("descri");
		Path<BigDecimal> produtoFrom_Qtdest = produtoFrom.get("qtdest");
		
		//Cláusula Select
		criteria.multiselect(produtoFrom_Id, produtoFrom_Descri, produtoFrom_Qtdest);
		
		//Cláusula Where
		Predicate produtoWhere = cb.equal(produtoFrom.get("client"), BigInteger.ONE);
		
		//Cláusula OrderBy
		Order produtoOrderBy = cb.asc(produtoFrom.get("descri"));
		
		criteria.where(produtoWhere);
		criteria.orderBy(produtoOrderBy);
				
		TypedQuery<Tuple> query = em.createQuery(criteria);
		query.setMaxResults(10);
		
		List<Tuple> tuples = query.getResultList();
		
		List<ProdutoVO> ret = new ArrayList<ProdutoVO>();
		if(tuples != null) {
			for(Tuple tuple : tuples) {
				ProdutoVO produtoVO = new ProdutoVO(tuple.get(produtoFrom_Id));
				produtoVO.setDescri(tuple.get(produtoFrom_Descri));
				produtoVO.setQtdest(tuple.get(produtoFrom_Qtdest));
				ret.add(produtoVO);
			}
		}
		
		for (ProdutoVO produtoVO : ret) {
			System.out.println("Produto: " + produtoVO.getId() + " - " 
					+ produtoVO.getDescri());
		}
		
		//Fecha o entity manager
		em.close();
		
		System.out.println("Término da consulta com tupla");
		
	}

	private void consultarProdutoSimples() {
		
		System.out.println("----------------começando----------------");
		
		EntityManager em = HibernateUtil.getEntityManager();
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ProdutoVO> criteria = cb.createQuery(ProdutoVO.class);
		
		//Cláusula From
		Root<ProdutoVO> produtoFrom = criteria.from(ProdutoVO.class);
		
		//Cláusula Where
		ClienteVO c1 = new ClienteVO();
		c1.setId(new BigInteger("1"));
		Predicate produtoWhere = cb.equal(produtoFrom.get("client"), c1);
		
		//Cláusula orderBy
		Order produtoOrderBy = cb.asc(produtoFrom.get("descri"));
		
		//Atribuindo as cláusulas à consulta
		criteria.select(produtoFrom);
		criteria.where(produtoWhere);
		criteria.orderBy(produtoOrderBy);
		
		TypedQuery<ProdutoVO> query = em.createQuery(criteria);
				
		List<ProdutoVO> listaProdutos = query.getResultList();
		
		for (ProdutoVO produtoVO : listaProdutos) {
			System.out.println("Produto: " + produtoVO.getId() + " - " 
					+ produtoVO.getDescri() + " - " + produtoVO.getClient().getDescri());
		}
		
		em.close();
		
		System.out.println("----------------terminou----------------");
		
	}

	private void consultarProdutoPorId() {
		
		EntityManager em = HibernateUtil.getEntityManager();
		
		ProdutoVO produto = em.find(ProdutoVO.class, new BigInteger("3"));
		
		System.out.println("Produto >>> " + produto.getId() + " - " + produto.getDescri());
		System.out.println(produto.getClient().getDescri());
		
		em.close();
		
	}

	private void excluirProduto() {
		// TODO Auto-generated method stub

	}

	private void editarProduto() {
		// TODO Auto-generated method stub

	}

	private void inserirProduto() {
		// TODO Auto-generated method stub

	}

	private void consultarProduto() {

		// TODO Auto-generated method stub

	}

}
