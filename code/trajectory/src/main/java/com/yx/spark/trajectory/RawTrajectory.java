/**
 * Autogenerated by Avro
 * 
 * DO NOT EDIT DIRECTLY
 */
package com.yx.spark.trajectory;  
@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public class RawTrajectory extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"RawTrajectory\",\"namespace\":\"com.yx.spark.trajectory\",\"fields\":[{\"name\":\"timeStamp\",\"type\":\"int\"},{\"name\":\"baseId\",\"type\":\"string\"}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }
  @Deprecated public int timeStamp;
  @Deprecated public CharSequence baseId;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>. 
   */
  public RawTrajectory() {}

  /**
   * All-args constructor.
   */
  public RawTrajectory(Integer timeStamp, CharSequence baseId) {
    this.timeStamp = timeStamp;
    this.baseId = baseId;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call. 
  public Object get(int field$) {
    switch (field$) {
    case 0: return timeStamp;
    case 1: return baseId;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
  // Used by DatumReader.  Applications should not call. 
  @SuppressWarnings(value="unchecked")
  public void put(int field$, Object value$) {
    switch (field$) {
    case 0: timeStamp = (Integer)value$; break;
    case 1: baseId = (CharSequence)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'timeStamp' field.
   */
  public Integer getTimeStamp() {
    return timeStamp;
  }

  /**
   * Sets the value of the 'timeStamp' field.
   * @param value the value to set.
   */
  public void setTimeStamp(Integer value) {
    this.timeStamp = value;
  }

  /**
   * Gets the value of the 'baseId' field.
   */
  public CharSequence getBaseId() {
    return baseId;
  }

  /**
   * Sets the value of the 'baseId' field.
   * @param value the value to set.
   */
  public void setBaseId(CharSequence value) {
    this.baseId = value;
  }

  /** Creates a new RawTrajectory RecordBuilder */
  public static Builder newBuilder() {
    return new Builder();
  }
  
  /** Creates a new RawTrajectory RecordBuilder by copying an existing Builder */
  public static Builder newBuilder(Builder other) {
    return new Builder(other);
  }
  
  /** Creates a new RawTrajectory RecordBuilder by copying an existing RawTrajectory instance */
  public static Builder newBuilder(RawTrajectory other) {
    return new Builder(other);
  }
  
  /**
   * RecordBuilder for RawTrajectory instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<RawTrajectory>
    implements org.apache.avro.data.RecordBuilder<RawTrajectory> {

    private int timeStamp;
    private CharSequence baseId;

    /** Creates a new Builder */
    private Builder() {
      super(RawTrajectory.SCHEMA$);
    }
    
    /** Creates a Builder by copying an existing Builder */
    private Builder(Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.timeStamp)) {
        this.timeStamp = data().deepCopy(fields()[0].schema(), other.timeStamp);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.baseId)) {
        this.baseId = data().deepCopy(fields()[1].schema(), other.baseId);
        fieldSetFlags()[1] = true;
      }
    }
    
    /** Creates a Builder by copying an existing RawTrajectory instance */
    private Builder(RawTrajectory other) {
            super(RawTrajectory.SCHEMA$);
      if (isValidValue(fields()[0], other.timeStamp)) {
        this.timeStamp = data().deepCopy(fields()[0].schema(), other.timeStamp);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.baseId)) {
        this.baseId = data().deepCopy(fields()[1].schema(), other.baseId);
        fieldSetFlags()[1] = true;
      }
    }

    /** Gets the value of the 'timeStamp' field */
    public Integer getTimeStamp() {
      return timeStamp;
    }
    
    /** Sets the value of the 'timeStamp' field */
    public Builder setTimeStamp(int value) {
      validate(fields()[0], value);
      this.timeStamp = value;
      fieldSetFlags()[0] = true;
      return this; 
    }
    
    /** Checks whether the 'timeStamp' field has been set */
    public boolean hasTimeStamp() {
      return fieldSetFlags()[0];
    }
    
    /** Clears the value of the 'timeStamp' field */
    public Builder clearTimeStamp() {
      fieldSetFlags()[0] = false;
      return this;
    }

    /** Gets the value of the 'baseId' field */
    public CharSequence getBaseId() {
      return baseId;
    }
    
    /** Sets the value of the 'baseId' field */
    public Builder setBaseId(CharSequence value) {
      validate(fields()[1], value);
      this.baseId = value;
      fieldSetFlags()[1] = true;
      return this; 
    }
    
    /** Checks whether the 'baseId' field has been set */
    public boolean hasBaseId() {
      return fieldSetFlags()[1];
    }
    
    /** Clears the value of the 'baseId' field */
    public Builder clearBaseId() {
      baseId = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    @Override
    public RawTrajectory build() {
      try {
        RawTrajectory record = new RawTrajectory();
        record.timeStamp = fieldSetFlags()[0] ? this.timeStamp : (Integer) defaultValue(fields()[0]);
        record.baseId = fieldSetFlags()[1] ? this.baseId : (CharSequence) defaultValue(fields()[1]);
        return record;
      } catch (Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }
}