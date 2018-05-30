package com.scs.service.impl;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scs.entity.model.CustomerDetails;
import com.scs.entity.model.ServerDetails;
import com.scs.exception.ApiException;
import com.scs.model.BaseRequestModel;
import com.scs.service.DBServices;
import com.scs.util.ErrorConstants;
import com.scs.util.Utility;

@Service("dbService")
@Transactional
public class DBServicesImpl implements DBServices {

	private static final Logger logger = Logger.getLogger(DBServicesImpl.class);

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	
	private MessageSource messageSource;

	@Override
	public Object getCustomerDetails(BaseRequestModel baseModel) throws ApiException {
		
		//Session session = null;
		//Transaction tx = null;
		
		CustomerDetails customerObj = null;
		
		try {
			//session = sessionFactory.openSession();
			//tx = session.beginTransaction();
			CriteriaBuilder builder = sessionFactory.getCurrentSession().getCriteriaBuilder();
			CriteriaQuery<CustomerDetails> select = builder.createQuery(CustomerDetails.class);
			Root<CustomerDetails> root = select.from(CustomerDetails.class);
			ParameterExpression<String> authCode = builder.parameter(String.class);
			Predicate authCodeP = builder.equal(root.get("authCode"), authCode);
			Predicate and1 = builder.and(authCodeP);
			select.where(and1);
			
			List<CustomerDetails> customerDao = sessionFactory.getCurrentSession().createQuery(select) .setParameter(authCode, baseModel.getAuthCode()).getResultList();
			
			String queryString = "from CustomerDetails";
			
			 TypedQuery<CustomerDetails> query = sessionFactory.getCurrentSession().createQuery(queryString);
			 List<CustomerDetails> result = query.getResultList();


			if(customerDao.isEmpty()){
				throw new ApiException(ErrorConstants.INVALID_AUTH_CODE, messageSource);
			}
			
			customerObj = customerDao.get(0);
			
		} catch (ApiException ex) {
			logger.error(ex.getErrorCode() + " : " + ex.getErrorMessage());
			throw ex;
		} catch (HibernateException ex) {
			logger.error("+++++ DBServicesImpl.getCustomerDetails END SERVICE WITH Hibernate EXCEPTION +++++");
			logger.error(Utility.getExceptionMessage(ex));
		} catch (Exception ex) {
			logger.error("+++++ DBServicesImpl.getCustomerDetails END SERVICE WITHEXCEPTION +++++");
			logger.error(Utility.getExceptionMessage(ex));
		} /*finally {
			if (session != null) {
				session.close();
			}
		}*/
		
		return customerObj;
	}

	@Override
	public Object getServerDetails(BaseRequestModel baseModel) throws ApiException {

		ServerDetails serverDetails = null;
		
		try {
//			CriteriaBuilder builder = sessionFactory.getCurrentSession().getCriteriaBuilder();
//			CriteriaQuery<ServerDetails> select = builder.createQuery(ServerDetails.class);
//			Root<ServerDetails> root = select.from(ServerDetails.class);
//			select.select(root);
//			
//			List<ServerDetails> serverDetails = sessionFactory.getCurrentSession().createQuery(select).getResultList();
			
			String queryString = "from ServerDetails";
			
			 TypedQuery<ServerDetails> query = sessionFactory.getCurrentSession().createQuery(queryString);
			 List<ServerDetails> result = query.getResultList();


			
			 serverDetails = (ServerDetails) result.get(0);
			
		} catch (HibernateException ex) {
			logger.error("+++++ DBServicesImpl.getCustomerDetails END SERVICE WITH Hibernate EXCEPTION +++++");
			logger.error(Utility.getExceptionMessage(ex));
		} catch (Exception ex) {
			logger.error("+++++ DBServicesImpl.getCustomerDetails END SERVICE WITHEXCEPTION +++++");
			logger.error(Utility.getExceptionMessage(ex));
		} /*finally {
			if (session != null) {
				session.close();
			}
		}*/
		
		return serverDetails;
	}
	


}
