package org.kuali.core.db.torque;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.torque.engine.platform.Platform;
import org.apache.torque.engine.platform.PlatformFactory;
import org.apache.xerces.dom.DocumentImpl;
import org.apache.xerces.dom.DocumentTypeImpl;
import org.apache.xml.serialize.Method;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Element;

public class KualiTorqueJDBCTransformTask extends Task {
	
	// some flags for making it easier to debug
	private final boolean processTables = true;
	private final boolean processViews = true;
	private final boolean processSequences = true;
	
	
    /** Name of XML database schema produced. */
    protected String xmlSchema;

    /** JDBC URL. */
    protected String dbUrl;

    /** JDBC driver. */
    protected String dbDriver;

    /** JDBC user name. */
    protected String dbUser;

    /** JDBC password. */
    protected String dbPassword;

    /** DB schema to use. */
    protected String dbSchema;

    /** DOM document produced. */
    protected DocumentImpl doc;

    /** The document root element. */
    protected Element databaseNode;

    protected String dbType;
    
    /** Hashtable of columns that have primary keys. */
    protected HashMap<String,String> primaryKeys;

    private XMLSerializer xmlSerializer;

	private String tableNameRegex = ".*";
    private String tableNameExcludeRegex = "";
    private Pattern tableNameRegexPattern = Pattern.compile(tableNameRegex);
    private Pattern tableNameExcludeRegexPattern = StringUtils.isBlank(tableNameExcludeRegex)?null:Pattern.compile(tableNameExcludeRegex);
    
    public String getDbSchema()
    {
        return dbSchema;
    }

    public void setDbSchema(String dbSchema)
    {
        this.dbSchema = dbSchema;
    }

    public void setDbUrl(String v)
    {
        dbUrl = v;
    }

    public void setDbDriver(String v)
    {
        dbDriver = v;
    }

    public void setDbUser(String v)
    {
        dbUser = v;
    }

    public void setDbPassword(String v)
    {
        dbPassword = v;
    }

    public void setOutputFile (String v)
    {
        xmlSchema = v;
    }
    
    public void setDbType(String dbType) {
		this.dbType = dbType;
	}

    /**
     * Default constructor.
     *
     * @throws BuildException
     */
    public void execute() throws BuildException {
        log("Torque - JDBCToXMLSchema starting");
        log("Your DB settings are:");
        log("driver : " + dbDriver);
        log("URL : " + dbUrl);
        log("user : " + dbUser);
        // log("password : " + dbPassword);
        log("schema : " + dbSchema);

        DocumentTypeImpl docType = new DocumentTypeImpl(null, "database", null, "database.dtd" );
        doc = new DocumentImpl(docType);
        doc.appendChild(doc.createComment( " Autogenerated by KualiTorqueJDBCTransformTask! "));

        try {
            generateXML();
            log(xmlSchema);
            OutputFormat of = new OutputFormat(Method.XML, null, true);
            of.setLineWidth(120);
            xmlSerializer = new XMLSerializer( new PrintWriter( new FileOutputStream(xmlSchema) ), of);
            xmlSerializer.serialize(doc);
        } catch (Exception e) {
            throw new BuildException(e);
        }
        log("Torque - JDBCToXMLSchema finished");
    }

    protected List<String> upperCaseList( List<String> sourceList ) {
    	ArrayList<String> newList = new ArrayList<String>( sourceList.size() );
    	for ( String item : sourceList ) {
    		if ( item != null ) {
    			newList.add( item.toUpperCase() );
    		} else {
    			newList.add( null );
    		}
    	}
    	return newList;
    }
    
	/**
	 * Generates an XML database schema from JDBC metadata.
	 * 
	 * @throws Exception
	 *             a generic exception.
	 */
	public void generateXML() throws Exception {
		// Load the database Driver.
		Class.forName( dbDriver );
		log( "DB driver sucessfuly instantiated" );

		Connection con = null;
		try {
			// Attempt to connect to a database.
			Properties p = new Properties();
			p.setProperty("user", dbUser);
			p.setProperty("password", dbPassword);
			p.setProperty("oracle.jdbc.mapDateToTimestamp", "false"); // workaround for change in 11g JDBC driver
			con = DriverManager.getConnection( dbUrl, p );
			log( "DB connection established" );

			Platform platform = PlatformFactory.getPlatformFor( dbType );
			
			// Get the database Metadata.
			DatabaseMetaData dbMetaData = con.getMetaData();
			
			databaseNode = doc.createElement( "database" );
			databaseNode.setAttribute( "name", "kuali" );
			// JHK added naming method
			databaseNode.setAttribute( "defaultJavaNamingMethod", "nochange" );

			if ( processTables ) {
				List<String> tableList = platform.getTableNames( dbMetaData, dbSchema );
				// ensure all are upper case before exporting
				tableList = upperCaseList(tableList);
				// ensure sorting is consistent (not DB-dependent)
				Collections.sort(tableList);
				for ( String curTable : tableList ) {
					if ( !tableNameRegexPattern.matcher( curTable ).matches() ) {
						log( "Skipping table: " + curTable);
						continue;
					}
                    if ( StringUtils.isNotBlank(tableNameExcludeRegex) && 
                            tableNameExcludeRegexPattern.matcher( curTable ).matches() ) {
                        log( "Skipping table: " + curTable);
                        continue;
                    }
					log( "Processing table: " + curTable );
	
					Element table = doc.createElement( "table" );
					table.setAttribute( "name", curTable.toUpperCase() );
	
					// Add Columns.
					// TableMap tblMap = dbMap.getTable(curTable);
	
					List columns = getColumns( dbMetaData, curTable );
					List<String> primKeys = platform.getPrimaryKeys( dbMetaData, dbSchema, curTable );
					Map<String,Object[]> foreignKeys = getForeignKeys( dbMetaData, curTable );
	
					// Set the primary keys.
					primaryKeys = new HashMap<String,String>();
	
					for ( int k = 0; k < primKeys.size(); k++ ) {
						String curPrimaryKey = (String)primKeys.get( k );
						primaryKeys.put( curPrimaryKey, curPrimaryKey );
					}
	
					for ( int j = 0; j < columns.size(); j++ ) {
						List col = (List)columns.get( j );
						String name = (String)col.get( 0 );
						Integer jdbcType = ((Integer)col.get( 1 ));
						int size = ((Integer)col.get( 2 )).intValue();
						int scale = ((Integer)col.get( 5 )).intValue();
	
						// From DatabaseMetaData.java
						//
						// Indicates column might not allow NULL values. Huh?
						// Might? Boy, that's a definitive answer.
						/* int columnNoNulls = 0; */
	
						// Indicates column definitely allows NULL values.
						/* int columnNullable = 1; */
	
						// Indicates NULLABILITY of column is unknown.
						/* int columnNullableUnknown = 2; */
	
						Integer nullType = (Integer)col.get( 3 );
						String defValue = (String)col.get( 4 );
	
						Element column = doc.createElement( "column" );
						column.setAttribute( "name", name );
	
						;
						column.setAttribute( "type", platform.getTorqueColumnType( jdbcType ) );
						//							TypeMap.getTorqueType( type ).getName() );
	
						if ( size > 0 &&
								(jdbcType.intValue() == Types.CHAR
								|| jdbcType.intValue() == Types.VARCHAR
								|| jdbcType.intValue() == Types.DECIMAL 
								|| jdbcType.intValue() == Types.NUMERIC) ) {
							column.setAttribute( "size", String.valueOf( size ) );
						}
	
						if ( scale > 0 &&
								(jdbcType.intValue() == Types.DECIMAL || jdbcType
										.intValue() == Types.NUMERIC) ) {
							column.setAttribute( "scale", String.valueOf( scale ) );
						}
	
						if ( primaryKeys.containsKey( name ) ) {
							column.setAttribute( "primaryKey", "true" );
							// JHK: protect MySQL from excessively long column in the PK
							//System.out.println( curTable + "." + name + " / " + size );
							if ( column.getAttribute( "size" ) != null 
									&& size > 765 ) {
								log( "updating column " + curTable + "." + name + " length from " + size + " to 255" );
								column.setAttribute( "size", "255" );
							}
						} else {
							if ( nullType.intValue() == DatabaseMetaData.columnNoNulls ) {
								column.setAttribute( "required", "true" );
							}
						}
	
						
						if ( StringUtils.isNotEmpty( defValue ) ) {
							defValue = platform.getColumnDefaultValue( platform.getTorqueColumnType( jdbcType ), defValue );
							if ( StringUtils.isNotEmpty( defValue ) ) {
								column.setAttribute( "default", defValue );
							}
						}
						table.appendChild( column );
					}
	
					List<String> foreignKeyNames = new ArrayList<String>( foreignKeys.keySet() );
					Collections.sort( foreignKeyNames );
					// Foreign keys for this table.
					for ( String fkName : foreignKeyNames ) {
						Element fk = doc.createElement( "foreign-key" );
						fk.setAttribute(  "name", fkName.toUpperCase() );
						Object[] forKey = foreignKeys.get( fkName );
						String foreignKeyTable = (String)forKey[0];
						List refs = (List)forKey[1];
						fk.setAttribute( "foreignTable", foreignKeyTable.toUpperCase() );
						String onDelete = (String) forKey[2];
						// gmcgrego - just adding onDelete if it's cascade so as not to affect kfs behavior
						if (onDelete == "cascade") {
							fk.setAttribute("onDelete", onDelete);
						}
						for ( int m = 0; m < refs.size(); m++ ) {
							Element ref = doc.createElement( "reference" );
							String[] refData = (String[])refs.get( m );
							ref.setAttribute( "local", refData[0] );
							ref.setAttribute( "foreign", refData[1] );
							fk.appendChild( ref );
						}
						table.appendChild( fk );
					}
					
					List<TableIndex> indexes = getIndexes( dbMetaData, curTable );
					Collections.sort( indexes, new Comparator<TableIndex>() {
						public int compare(TableIndex o1, TableIndex o2) { return o1.name.compareTo(o2.name); }
					} );
					for ( TableIndex idx : indexes ) {
						if ( foreignKeyNames.contains( idx.name ) ) {
							log( idx.name + " is also a foreign key, skipping" );
							continue;
						}
						String tagName = idx.unique?"unique":"index";
						Element index = doc.createElement( tagName );
						index.setAttribute( "name", idx.name.toUpperCase() );
						for ( String colName : idx.columns ) {
							Element col = doc.createElement( tagName + "-column" );
							col.setAttribute( "name", colName );
							index.appendChild( col );
						}
						table.appendChild( index );
					}
					
					databaseNode.appendChild( table );
				}
			}
			if ( processViews ) {
				log( "Getting view list..." );
				List<String> viewNames = platform.getViewNames( dbMetaData, dbSchema );
				log( "Found " + viewNames.size() + " views." );
				viewNames = upperCaseList(viewNames);
				Collections.sort( viewNames );
				for ( String viewName : viewNames ) {
					if ( !tableNameRegexPattern.matcher( viewName ).matches() ) {
						log( "Skipping view: " + viewName);
						continue;
					}
					Element view = doc.createElement( "view" );
					view.setAttribute( "name", viewName.toUpperCase() );
					/*
					 * <view name="" viewdefinition="" />
					 * 
					 */
					String definition = platform.getViewDefinition( dbMetaData.getConnection(), dbSchema, viewName );
					definition = definition.replaceAll( "\0", "" );
					view.setAttribute( "viewdefinition", definition );
					databaseNode.appendChild( view );
				}
			}
			
			if ( processSequences ) {
				log( "Getting sequence list..." );
				List<String> sequenceNames = platform.getSequenceNames( dbMetaData, dbSchema );
				log( "Found " + sequenceNames.size() + " sequences." );
				sequenceNames = upperCaseList(sequenceNames);
				Collections.sort( sequenceNames );
				for ( String sequenceName : sequenceNames ) {
					if ( !tableNameRegexPattern.matcher( sequenceName ).matches() ) {
						log( "Skipping sequence: " + sequenceName);
						continue;
					}
					Element sequence = doc.createElement( "sequence" );
					sequence.setAttribute( "name", sequenceName.toUpperCase() );
					/*
					 * <view name="" nextval="" />
					 * 
					 */
					Long nextVal = platform.getSequenceNextVal( dbMetaData.getConnection(), dbSchema, sequenceName );
					sequence.setAttribute( "nextval", nextVal.toString() );
					
					databaseNode.appendChild( sequence );
				}
				doc.appendChild( databaseNode );
			}
		} finally {
			if ( con != null ) {
				con.close();
				con = null;
			}
		}
	}

    /**
     * Retrieves all the column names and types for a given table from
     * JDBC metadata.  It returns a List of Lists.  Each element
     * of the returned List is a List with:
     *
     * element 0 => a String object for the column name.
     * element 1 => an Integer object for the column type.
     * element 2 => size of the column.
     * element 3 => null type.
     *
     * @param dbMeta JDBC metadata.
     * @param tableName Table from which to retrieve column information.
     * @return The list of columns in <code>tableName</code>.
     * @throws SQLException
     */
    public List<List<Object>> getColumns(DatabaseMetaData dbMeta, String tableName)
            throws SQLException
    {
        List<List<Object>> columns = new ArrayList<List<Object>>();
        ResultSet columnSet = null;
        try
        {
            columnSet = dbMeta.getColumns(null, dbSchema, tableName, null);
            while (columnSet.next())
            {
                String name = columnSet.getString(4);
                Integer sqlType = new Integer(columnSet.getString(5));
                Integer size = new Integer(columnSet.getInt(7));
                Integer decimalDigits = new Integer(columnSet.getInt(9));
                Integer nullType = new Integer(columnSet.getInt(11));
                String defValue = columnSet.getString(13);

                List<Object> col = new ArrayList<Object>(6);
                col.add(name);
                col.add(sqlType);
                col.add(size);
                col.add(nullType);
                col.add(defValue);
                col.add(decimalDigits);
                columns.add(col);
            }
        }
        finally
        {
            if (columnSet != null)
            {
                columnSet.close();
            }
        }
        return columns;
    }


    /**
     * Retrieves a list of foreign key columns for a given table.
     *
     * @param dbMeta JDBC metadata.
     * @param tableName Table from which to retrieve FK information.
     * @return A list of foreign keys in <code>tableName</code>.
     * @throws SQLException
     */
    public Map<String,Object[]> getForeignKeys(DatabaseMetaData dbMeta, String tableName)
        throws SQLException
    {
        TreeMap<String,Object[]> fks = new TreeMap<String,Object[]>();
        ResultSet foreignKeys = null;
        try
        {
            foreignKeys = dbMeta.getImportedKeys(null, dbSchema, tableName);
            while (foreignKeys.next())
            {
                String refTableName = foreignKeys.getString(3);
                String fkName = foreignKeys.getString(12);
                int deleteRule = foreignKeys.getInt(11);
                String onDelete = "none";
                if (deleteRule == DatabaseMetaData.importedKeyCascade) {
                	onDelete = "cascade";
                } else if (deleteRule == DatabaseMetaData.importedKeyRestrict) {
                	onDelete = "restrict";
                } else if (deleteRule == DatabaseMetaData.importedKeySetNull) {
                	onDelete = "setnull";
                }
                // if FK has no name - make it up (use tablename instead)
                if (fkName == null)
                {
                    fkName = refTableName;
                }
                Object[] fk = (Object[]) fks.get(fkName);
                List<String[]> refs;
                if (fk == null)
                {
                    fk = new Object[3];
                    fk[0] = refTableName; //referenced table name
                    refs = new ArrayList<String[]>();
                    fk[1] = refs;
                    fks.put(fkName, fk);
                    fk[2] = onDelete;
                }
                else
                {
                    refs = (ArrayList<String[]>) fk[1];
                }
                String[] ref = new String[2];
                ref[0] = foreignKeys.getString(8); //local column
                ref[1] = foreignKeys.getString(4); //foreign column
                refs.add(ref);
            }
        }
        catch (SQLException e)
        {
            // this seems to be happening in some db drivers (sybase)
            // when retrieving foreign keys from views.
            log("WARN: Could not read foreign keys for Table "
                        + tableName
                        + " : "
                        + e.getMessage(),
                    Project.MSG_WARN);
        }
        finally
        {
            if (foreignKeys != null)
            {
                foreignKeys.close();
            }
        }
        return fks;
    }
    
    public List<TableIndex> getIndexes( DatabaseMetaData dbMeta, String tableName ) throws SQLException {
    	List<TableIndex> indexes = new ArrayList<TableIndex>();
    	ResultSet pkInfo = null;
    	String pkName = null;
    	//ArrayList<String> pkFields = new ArrayList<String>();
    	ResultSet indexInfo = null;
    	try {
    		indexInfo = dbMeta.getIndexInfo( null, dbSchema, tableName, false, true );
    		// need to ensure that the PK is not returned as an index
    		pkInfo = dbMeta.getPrimaryKeys( null, dbSchema, tableName );
    		if ( pkInfo.next() ) {
    			pkName = pkInfo.getString( "PK_NAME" );
    		}
    		//Map<Integer,String> tempPk = new HashMap<Integer,String>(); 
    		//while ( pkInfo.next() ) {
    		//	tempPk.put( pkInfo.getInt( "KEY_SEQ" ), pkInfo.getString( "COLUMN_NAME" ) );
    		//}
    		
    		TableIndex currIndex = null;
    		while ( indexInfo.next() ) {
    			if ( indexInfo.getString( "INDEX_NAME" ) == null ) continue;
    			//System.out.println( "Row: " + indexInfo.getString( "INDEX_NAME" ) + "/" + indexInfo.getString( "COLUMN_NAME" ) );
    			if ( currIndex == null || !indexInfo.getString( "INDEX_NAME" ).equals( currIndex.name ) ) {
    				currIndex = new TableIndex();
    				currIndex.name = indexInfo.getString( "INDEX_NAME" );
    				currIndex.unique = !indexInfo.getBoolean( "NON_UNIQUE" );
    				// if has the same name as the PK, skip adding it to the index list
    				if ( pkName == null || !pkName.equals( currIndex.name ) ) {
    					indexes.add( currIndex );
    					//System.out.println( "Added " + currIndex.name + " to index list");
    				} else {
    					//System.out.println( "Skipping PK: " + currIndex.name );
    				}
    			}
    			currIndex.columns.add( indexInfo.getString( "COLUMN_NAME" ) );
    		}
    		
    	} catch (SQLException e) {
            log("WARN: Could not read indexes for Table "
                    + tableName
                    + " : "
                    + e.getMessage(),
                Project.MSG_WARN);
		} finally {
			if ( indexInfo != null ) {
				indexInfo.close();
			}
			if ( pkInfo != null ) {
				pkInfo.close();
			}
		}
		return indexes;
    }
	
    private static class TableIndex {
    	public String name;
    	public boolean unique;
    	public List<String> columns = new ArrayList<String>( 10 );
    }

	public String getTableNameRegex() {
		return tableNameRegex;
	}

	public void setTableNameRegex(String tableNameRegex) {
		this.tableNameRegex = tableNameRegex;
		tableNameRegexPattern = Pattern.compile(tableNameRegex);
	}

    public String getTableNameExcludeRegex() {
        return tableNameExcludeRegex;
    }
    public void setTableNameExcludeRegex(String tableNameExcludeRegex) {
        this.tableNameExcludeRegex = tableNameExcludeRegex;
        tableNameExcludeRegexPattern = Pattern.compile(tableNameExcludeRegex);
    }
    public Pattern getTableNameExcludeRegexPattern() {
        return tableNameExcludeRegexPattern;
    }
}
