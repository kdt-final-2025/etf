export interface FetchResult<T> {
  data: T | null;
  error: string | null;
  status?: number;
}

interface ApiRequestConfig extends Omit<RequestInit, "method"> {
  baseUrl?: string;
  errorMessage?: string;
  authToken?: string;
  timeout?: number;
}

interface BodyRequestConfig extends Omit<ApiRequestConfig, "body"> {}

interface InternalRequestConfig extends ApiRequestConfig {
  method: string;
}

async function fetchApi<T>(
  endpoint: string,
  config: InternalRequestConfig
): Promise<FetchResult<T>> {
  const {
    baseUrl = getDefaultBaseUrl(),
    errorMessage,
    authToken,
    method,
    headers: customHeaders = {},
    body,
    timeout = parseInt(process.env.NEXT_PUBLIC_API_TIMEOUT || "10000", 10),
    ...restConfig
  } = config;

  validateBaseUrl(baseUrl);
  const url = buildRequestUrl(baseUrl, endpoint);
  const headers = prepareHeaders(customHeaders, authToken);

  const controller = new AbortController();
  const timeoutId = setTimeout(() => controller.abort(), timeout); // 일정 시간 초과 시 요청 취소

  try {
    const response = await fetch(url, {
      method,
      headers,
      body,
      ...restConfig,
    });

    clearTimeout(timeoutId);

    if (!response.ok) {
      return await handleErrorResponse(response, url, errorMessage);
    }

    const contentType = response.headers.get("content-type") || "";
    if (!contentType.includes("application/json")) {
      return handleNonJsonResponse(response, contentType);
    }

    return await parseJsonResponse<T>(response);
  } catch (error) {
    clearTimeout(timeoutId);

    console.error(`Error fetching ${method} ${url}:`, {
      headers: Object.fromEntries(headers),
      body,
      error,
    });

    return {
      data: null,
      error:
        errorMessage ||
        "서비스를 이용할 수 없습니다. 나중에 다시 시도해 주세요.",
    };
  }
}

function getDefaultBaseUrl(): string {
  const envUrl = process.env.NEXT_PUBLIC_API_BASE_URL;
  const fallbackUrl =
    process.env.NODE_ENV === "development" ? "http://localhost:8080" : "";
  return envUrl || fallbackUrl;
}

function validateBaseUrl(baseUrl: string): void {
  if (!baseUrl) {
    throw new Error(
      "NEXT_PUBLIC_API_BASE_URL 값이 환경 변수로 정의되지 않았습니다."
    );
  }
}

function buildRequestUrl(baseUrl: string, endpoint: string): string {
  return endpoint.startsWith("http") ? endpoint : `${baseUrl}${endpoint}`;
}

function prepareHeaders(
  customHeaders: HeadersInit,
  authToken?: string
): Headers {
  const headers = new Headers(customHeaders);
  if (authToken) {
    headers.set("Authorization", `Bearer ${authToken}`);
  }
  return headers;
}

async function handleErrorResponse(
  response: Response,
  url: string,
  errorMessage?: string
): Promise<never> {
  const status = response.status;
  const errorBody = await response.text().catch(() => "알 수 없는 에러");

  console.error(`API 오류: (${status}): ${url}`, errorBody);

  throw new Error(
    errorMessage ||
      getDefaultErrorMessage(status) ||
      `HTTP 에러 ${status}: ${errorBody}`
  );
}

function handleNonJsonResponse(
  response: Response,
  contentType: string
): FetchResult<any> {
  console.warn(
    `서버에서 JSON이 아닌 응답을 반환했습니다. Content-Type: ${contentType}`
  );
  return {
    data: null,
    error: "서버에서 예상치 못한 응답 형식을 반환했습니다.",
    status: response.status,
  };
}

async function parseJsonResponse<T>(
  response: Response
): Promise<FetchResult<T>> {
  try {
    const data = await response.json();
    return {
      data,
      error: null,
      status: response.status,
    };
  } catch (parseError) {
    console.error("JSON 파싱 오류:", parseError);
    return {
      data: null,
      error:
        "서버 응답을 처리하는 중 오류가 발생했습니다: 유효하지 않은 JSON 형식",
      status: response.status,
    };
  }
}

function getDefaultErrorMessage(status: number): string {
  const defaultErrorMessages: Record<number, string> = {
    400: "잘못된 요청입니다. 입력을 확인해주세요.",
    401: "인증에 실패했습니다. 다시 로그인해주세요.",
    403: "이 작업을 수행할 권한이 없습니다.",
    404: "요청한 리소스를 찾을 수 없습니다.",
    500: "서버 오류가 발생했습니다. 나중에 다시 시도해주세요.",
  };
  return defaultErrorMessages[status] || `HTTP Error ${status}`;
}

export function addQueryParams(
  url: string,
  params: Record<string, any>
): string {
  const urlObj = new URL(url);

  Object.entries(params).forEach(([key, value]) => {
    if (value === undefined || value === null) return;

    if (Array.isArray(value)) {
      value.forEach((item) => urlObj.searchParams.append(key, String(item)));
    } else {
      urlObj.searchParams.append(key, String(value));
    }
  });

  return urlObj.toString();
}

export function get<T>(
  endpoint: string,
  config?: ApiRequestConfig & { params?: Record<string, any> }
): Promise<FetchResult<T>> {
  const { params, ...restConfig } = config || {};

  if (params && Object.keys(params).length > 0) {
    endpoint = addQueryParams(endpoint, params);
  }

  return fetchApi<T>(endpoint, { ...restConfig, method: "GET" });
}

export function post<T, U>(
  endpoint: string,
  data: T,
  config?: BodyRequestConfig
): Promise<FetchResult<U>> {
  const headers = {
    "Content-Type": "application/json",
    ...(config?.headers || {}),
  };
  return fetchApi<U>(endpoint, {
    ...config,
    method: "POST",
    headers,
    body: JSON.stringify(data),
  });
}
