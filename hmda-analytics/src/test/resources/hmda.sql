CREATE DATABASE hmda
  WITH OWNER = postgres
       ENCODING = 'UTF8'
       TABLESPACE = pg_default
       LC_COLLATE = 'en_US.utf8'
       LC_CTYPE = 'en_US.utf8'
       CONNECTION LIMIT = -1;

CREATE TABLE public.transmittalsheet2018 (
    id integer NOT NULL,
    institution_name character varying NOT NULL,
    year integer,
    quarter integer,
    name character varying,
    phone character varying,
    email character varying,
    street character varying,
    city character varying,
    state character varying,
    zip_code character varying,
    agency integer,
    total_lines integer,
    tax_id character varying,
    lei character varying NOT NULL,
    created_at timestamp default current_timestamp,
    CONSTRAINT ts2018_pkey PRIMARY KEY (lei)
)
WITH (
  OIDS=FALSE
);

CREATE TABLE public.loanapplicationregister2018
(
    id integer NOT NULL,
    lei character varying NOT NULL,
    uli character varying,
    application_date character varying,
    loan_type integer,
    loan_purpose integer,
    preapproval integer,
    construction_method character varying,
    occupancy_type integer,
    loan_amount numeric,
    action_taken_type integer,
    action_taken_date integer,
    street character varying,
    city character varying,
    state character varying,
    zip character varying,
    county character varying,
    tract character varying,
    ethnicity_applicant_1 integer,
    ethnicity_applicant_2 integer,
    ethnicity_applicant_3 integer,
    ethnicity_applicant_4 integer,
    ethnicity_applicant_5 integer,
    other_hispanic_applicant character varying,
    ethnicity_co_applicant_1 integer,
    ethnicity_co_applicant_2 integer,
    ethnicity_co_applicant_3 integer,
    ethnicity_co_applicant_4 integer,
    ethnicity_co_applicant_5 integer,
    other_hispanic_co_applicant character varying,
    ethnicity_observed_applicant int,
    ethnicity_observed_co_applicant int,
    race_applicant_1 integer,
    race_applicant_2 integer,
    race_applicant_3 integer,
    race_applicant_4 integer,
    race_applicant_5 integer,
    other_native_race_applicant character varying,
    other_asian_race_applicant character varying,
    other_pacific_race_applicant character varying,
    race_co_applicant_1 integer,
    race_co_applicant_2 integer,
    race_co_applicant_3 integer,
    race_co_applicant_4 integer,
    race_co_applicant_5 integer,
    other_native_race_co_applicant character varying,
    other_asian_race_co_applicant character varying,
    other_pacific_race_co_applicant character varying,
    race_observed_applicant integer,
    race_observed_co_applicant integer,
    sex_applicant integer,
    sex_co_applicant integer,
    observed_sex_applicant integer,
    observed_sex_co_applicant integer,
    age_applicant integer,
    age_co_applicant integer,
    income character varying,
    purchaser_type integer,
    rate_spread character varying,
    hoepa_status integer,
    lien_status integer,
    credit_score_applicant integer,
    credit_score_co_applicant integer,
    credit_score_type_applicant integer,
    credit_score_model_applicant character varying,
    credit_score_type_co_applicant integer,
    credit_score_model_co_applicant character varying,
    denial_reason1 integer,
    denial_reason2 integer,
    denial_reason3 integer,
    denial_reason4 integer,
    other_denial_reason character varying,
    total_loan_costs character varying,
    total_points character varying,
    origination_charges character varying,
    discount_points character varying,
    lender_credits character varying,
    interest_rate character varying,
    payment_penalty character varying,
    debt_to_incode character varying,
    loan_value_ratio character  varying,
    loan_term character varying,
    rate_spread_intro character varying,
    baloon_payment integer,
    insert_only_payment integer,
    amortization integer,
    other_amortization integer,
    property_value character varying,
    home_security_policy integer,
    lan_property_interest integer,
    total_uits integer,
    mf_affordable character varying,
    application_submission integer,
    payable integer,
    nmls character varying,
    aus1 integer,
    aus2 integer,
    aus3 integer,
    aus4 integer,
    aus5 integer,
    other_aus character varying,
    aus1_result integer,
    aus2_result integer,
    aus3_result integer,
    aus4_result integer,
    aus5_result integer,
    other_aus_result character varying,
    reverse_mortgage integer,
    line_of_credits integer,
    business_or_commercial integer,
    created_at timestamp default current_timestamp
)
WITH (
    OIDS = FALSE
);

CREATE TABLE hmda_user.submission_history
(
    lei character varying COLLATE  NOT NULL,
    submission_id character varying  NOT NULL,
    sign_date bigint, --this will be epoc date
    CONSTRAINT sh2018_pkey PRIMARY KEY (lei, submission_id)
)
WITH (
    OIDS = FALSE
)

ALTER TABLE public.loanapplicationregister2018
    OWNER to postgres;

ALTER TABLE public.transmittalsheet2018
  OWNER TO postgres;