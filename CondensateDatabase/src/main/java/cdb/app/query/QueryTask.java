package cdb.app.query;

import java.util.ArrayList;
import java.util.List;

import cdb.dal.vo.DenseMatrix;

/**
 * 
 * @author Chao Chen
 * @version $Id: QueryTask.java, v 0.1 Aug 17, 2015 2:15:28 PM chench Exp $
 */
public class QueryTask {

    /** the domain to query*/
    private List<Position2D>     queryDomain;
    /** the domain contains the partial results position*/
    private List<Position2D>     completeDomain;
    /** the context need to query*/
    private List<DenseMatrix> queryContext;

    /**
     * Construction
     */
    public QueryTask() {
        queryDomain = new ArrayList<Position2D>();
        completeDomain = new ArrayList<Position2D>();
        queryContext = new ArrayList<DenseMatrix>();
    }

    public void add2QueryDomain(int x, int y) {
        queryDomain.add(new Position2D(x, y));
    }

    public void add2CompleteDomain(int x, int y) {
        completeDomain.add(new Position2D(x, y));
    }

    /**
     * Getter method for property <tt>queryDomain</tt>.
     * 
     * @return property value of queryDomain
     */
    public List<Position2D> getQueryDomain() {
        return queryDomain;
    }

    /**
     * Setter method for property <tt>queryDomain</tt>.
     * 
     * @param queryDomain value to be assigned to property queryDomain
     */
    public void setQueryDomain(List<Position2D> queryDomain) {
        this.queryDomain = queryDomain;
    }

    /**
     * Getter method for property <tt>completeDomain</tt>.
     * 
     * @return property value of completeDomain
     */
    public List<Position2D> getCompleteDomain() {
        return completeDomain;
    }

    /**
     * Setter method for property <tt>completeDomain</tt>.
     * 
     * @param completeDomain value to be assigned to property completeDomain
     */
    public void setCompleteDomain(List<Position2D> completeDomain) {
        this.completeDomain = completeDomain;
    }

    /**
     * Getter method for property <tt>queryContext</tt>.
     * 
     * @return property value of queryContext
     */
    public List<DenseMatrix> getQueryContext() {
        return queryContext;
    }

    /**
     * Setter method for property <tt>queryContext</tt>.
     * 
     * @param queryContext value to be assigned to property queryContext
     */
    public void setQueryContext(List<DenseMatrix> queryContext) {
        this.queryContext = queryContext;
    }

}
