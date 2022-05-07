package com.toeic.online.commons;

import com.toeic.online.constant.AppConstants;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Check String character input
 */
public class StringUtils {

	public static final String EMPTY = "";

	/**
	 * Check string not null or empty
	 *
	 * @return if string value not null or empty then return true else return false
	 */
	public static boolean isNotNullOrEmpty( String value ) {
		return value != null && !EMPTY.equals(value.trim());
	}

	public static boolean isNullOrEmpty( String str ) {
		return str == null || EMPTY.equals(str.trim());
	}

	public static boolean isNullOrEmpty(List<?> lst){
	    if(lst == null || lst.isEmpty()) return true;
	    else return false;
    }

	/**
	 * Passing parameter to string pattern
	 *
	 * @param pattern
	 * @param arguments
	 * @return string passing arguments
	 */
	public static String format( String pattern, Object... arguments ) {
		// begin replace text inside {...} to index
		// eg: {abc} blah blah {def} -> {0} blah blah {1}
		int index = pattern.indexOf("{");
		StringBuilder sb = new StringBuilder();
		int i = 0;
		int cursor = 0;
		while( index != -1 ) {
			sb.append(pattern.substring(cursor, index + 1)).append(i++).append("}");
			cursor = pattern.indexOf("}", cursor + 1) + 1;
			index = pattern.indexOf("{", index + 1);
		}
		sb.append(pattern.substring(pattern.lastIndexOf("}") + 1));
		// end

		return MessageFormat.format(sb.toString(), arguments);
	}

	public static String concatDateHourMinute( String date, Long hour, Long minute ) {
		StringBuilder sb = new StringBuilder();
		sb.append(date).append(" ");
		if( hour != null && hour < 10 ) {
			sb.append("0");
		}
		sb.append(hour).append(":");
		if( minute != null && minute < 10 ) {
			sb.append("0");
		}
		sb.append(minute).append(":00");
		return sb.toString();
	}

	/**
	 * String to List<String>
	 *
	 * @param source    String to split
	 * @param delimiter delimiter character
	 * @return List<String> with element not null or empty
	 */
	public static List<String> stringToList( String source, final String delimiter ) {
		List<String> list = new ArrayList<>();
		if( isNotNullOrEmpty(source) ) {
			String[] array = source.split(delimiter);
			for( String s : array ) {
				if( isNotNullOrEmpty(s) ) {
					list.add(s.trim());
				}
			}
		}
		return list.stream().distinct().collect(Collectors.toList());
	}

	public static String convertParamUpperCase( String value ) {
		StringBuilder sb = new StringBuilder("%");
		sb.append(value.toUpperCase()).append("%");
		return sb.toString();
	}

	public static String convertParamSearchLike( String value ) {
		StringBuilder sb = new StringBuilder("%");
		sb.append(value).append("%");
		return sb.toString();
	}

	/**
	 * Check value is only number
	 *
	 * @param value String
	 * @return true if value is number else return false
	 */
	public static boolean isOnlyNumber( String value ) {
		return value.matches("^[0-9]+$");
	}

	public static boolean isOnlyNumberAndCommand( String value ) {
		return value.matches("^[0-9 ,]+$");
	}

	public static boolean isCharacter( String value ) {
		return value.matches("^[a-zA-Z0-9]+$");
	}

	public static boolean isCharacterAndCommand( String value ) {
		return value.matches("^[a-zA-Z0-9 ,]+$");
	}

	/**
	 * - <code>BLANK_STRING_VALUE = ""</code></br>
	 * - If source is null return <code>StringUtils.BLANK_STRING_VALUE</code> else return source
	 *
	 * @param source String
	 * @return String
	 */
	public static String ifNullToEmpty( String source ) {
		return source == null ? EMPTY : source;
	}

	/**
	 * if value is numberic and maxlength <= max return true else return false
	 *
	 * @param value
	 * @param max
	 * @return
	 */
	public static boolean isNumberAndMaxlength( String value, int max ) {
		if( StringUtils.isNotNullOrEmpty(value) )
			return value.trim().length() <= max && isOnlyNumber(value);
		return true;
	}

	public static boolean isListNotNullOrEmpty( List<?> list ) {
		return list != null && list.size() > 0;
	}

	public static Integer parseIntIfNull( String value ) {
		return isNotNullOrEmpty(value) ? Integer.valueOf(value) : null;
	}

	public static BigInteger parseBigIntegerIfNull( String value ) {
		return isNotNullOrEmpty(value) ? new BigInteger(value) : null;
	}

	public static String convertParamLikeEnd( String value ) {
		StringBuilder sb = new StringBuilder(value);
		sb.append("%");
		return sb.toString();
	}

	/**
	 * getSortType
	 *
	 * @param sortStr flag check
	 * @return "ASC" OR "DESC"
	 */
	public static String getSortType( String sortStr ) {
		return AppConstants.SORT_DESC.equals(sortStr) ? AppConstants.SORT_DESC : AppConstants.SORT_ASC;
	}

	public static Long parseLongIfNull( String value ) {
		if( StringUtils.isNotNullOrEmpty(value) )
			return Long.valueOf(value);
		return 0L;
	}

	/**
	 * Insert str to String
	 *
	 * @param source String
	 * @param offset int
	 * @param str    String
	 * @return ex: source ="source", offset=2, str="." => return "so.urce"
	 */
	public static String insert( String source, int offset, String str ) {
		if( !isNotNullOrEmpty(source) )
			return null;
		return new StringBuilder(source).insert(offset, str).toString();
	}

	public static String convertStringToUTF8( String value ) throws UnsupportedEncodingException {
		if( isNotNullOrEmpty(value) ) {
			byte[] toByte = value.getBytes();
			String str = new String(toByte, StandardCharsets.UTF_8);
			return str;
		}
		return null;
	}

	public static boolean isNotNullandNumberMaxLength( String value, int max ) {
		if( StringUtils.isNotNullOrEmpty(value) )
			return value.trim().length() <= max && isOnlyNumber(value);
		return false;
	}

	/**
	 * Standard postal code
	 *
	 * @param postalCode String
	 * @return postal code is standard for display(1000000 -> 〒100-0000)
	 */
	public static String standardPostalCode( String postalCode ) {
		if( !isNotNullOrEmpty(postalCode) )
			return postalCode;
		if( postalCode.indexOf("-") == -1) {
			postalCode = insert(postalCode, 3, "-");
		}
		return String.format("〒%s", postalCode);
  }

	public static String calInstallTerm() {
		LocalDate today = LocalDate.now();
		int monthOfYear = today.getMonthValue();
		int year = today.getYear();
		if( monthOfYear > 4 ) {
			return year + 1 + "04";
		} else {
			return year + "04";
		}
	}


	/**
	 * Remove zero character before number meaning
	 *
	 * @param memberId String
	 * @return member id after remove zero
	 */
	public static String truncMemberId( String memberId ) {
		if( isNullOrEmpty(memberId) )
			return null;
		if( memberId.contains("L") ) {
			return String.format("L%d", Long.valueOf(memberId.substring(1)));
		} else {
			return Long.valueOf(memberId).toString();
		}
	}

    public static boolean isNullOrEmpty(Map<?, ?> map) {
        return (map == null || map.isEmpty());
    }
}
