export interface IRegisterUser {
  name: string;
  email: string;
  password: string;
  confirmPassword: string;
}

export interface ILoginUser {
  email: string;
  password: string;
}
  
export interface ILoginResponse {
  token: string;
  type: string;
}